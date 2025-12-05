package org.bread_experts_group.project_incubator.dns

import org.bread_experts_group.Mappable.Companion.id
import org.bread_experts_group.MappedEnumeration
import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.api.system.socket.BSLSocket
import org.bread_experts_group.api.system.socket.SystemSocketProviderFeatures
import org.bread_experts_group.api.system.socket.close.StandardCloseFeatures
import org.bread_experts_group.api.system.socket.ipv4.InternetProtocolV4AddressPortData
import org.bread_experts_group.api.system.socket.ipv6.IPv6SocketFeatures
import org.bread_experts_group.api.system.socket.ipv6.InternetProtocolV6AddressData
import org.bread_experts_group.api.system.socket.ipv6.InternetProtocolV6AddressPortData
import org.bread_experts_group.api.system.socket.ipv6.SystemInternetProtocolV6SocketProviderFeatures
import org.bread_experts_group.api.system.socket.ipv6.config.WindowsIPv6SocketConfigurationFeatures
import org.bread_experts_group.api.system.socket.ipv6.datagram.SystemInternetProtocolV6DatagramProtocolFeatures
import org.bread_experts_group.api.system.socket.ipv6.datagram.udp.IPv6UDPFeatures
import org.bread_experts_group.api.system.socket.ipv6.stream.SystemInternetProtocolV6StreamProtocolFeatures
import org.bread_experts_group.api.system.socket.ipv6.stream.tcp.IPv6TCPFeatures
import org.bread_experts_group.api.system.socket.listen.ReceiveSizeData
import org.bread_experts_group.api.system.socket.resolution.ResolutionDataPart
import org.bread_experts_group.api.system.socket.resolution.ResolutionDataPartIdentifier
import org.bread_experts_group.api.system.socket.resolution.WindowsResolutionFeatures
import org.bread_experts_group.ffi.windows.WindowsLastErrorException
import org.bread_experts_group.logging.ColoredHandler
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.nio.ByteBuffer
import kotlin.experimental.or
import kotlin.random.Random
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun main() {
	val logger = ColoredHandler.newLogger("TMP logger")
	val netSockets = SystemProvider.get(SystemFeatures.NETWORKING_SOCKETS)
	val udpV6 = netSockets
		.get(SystemSocketProviderFeatures.INTERNET_PROTOCOL_V6)
		.get(SystemInternetProtocolV6SocketProviderFeatures.DATAGRAM_PROTOCOLS)
		.get(SystemInternetProtocolV6DatagramProtocolFeatures.USER_DATAGRAM_PROTOCOL)
	val tcpV6 = netSockets
		.get(SystemSocketProviderFeatures.INTERNET_PROTOCOL_V6)
		.get(SystemInternetProtocolV6SocketProviderFeatures.STREAM_PROTOCOLS)
		.get(SystemInternetProtocolV6StreamProtocolFeatures.TRANSMISSION_CONTROL_PROTOCOL)
	val tcpV6Sockets = tcpV6.get(IPv6TCPFeatures.SOCKET)
	val udpV6Sockets = udpV6.get(IPv6UDPFeatures.SOCKET)

	fun writeMessage(buffer: ByteBuffer, message: DNSMessage) {
		val startPosition = buffer.position()
		buffer.putShort(message.identifier)
		buffer.putShort(message.flagBits)
		buffer.putShort(message.questions.size.toShort())
		buffer.putShort(message.answerRecords.size.toShort())
		buffer.putShort(message.authorityRecords.size.toShort())
		buffer.putShort(message.additionalRecords.size.toShort())
		var length = 0
		val label = ByteArray(63)
		val backReferences = mutableMapOf<String, Int>()
		fun writeDomain(domain: String) {
			if (domain == ".") {
				buffer.put(0)
				return
			}
			val domain = if (domain.endsWith('.')) domain else "$domain."
			val backReference = backReferences[domain]
			var backRefLocation = buffer.position() - startPosition
			if (backReference != null) {
				buffer.putShort(backReference.toShort() or 0b11000000_00000000.toShort())
				return
			} else if (backRefLocation <= 0b00111111_11111111)
				backReferences[domain] = backRefLocation
			for (index in domain.indices) {
				val char = domain[index]
				if (char == '.') {
					buffer.put(length.toByte())
					buffer.put(label, 0, length)
					length = 0
					backRefLocation = buffer.position() - startPosition
					if (index == domain.lastIndex) buffer.put(0)
					else {
						val backReference = backReferences[domain.substring(index + 1)]
						if (backReference != null) {
							buffer.putShort(backReference.toShort() or 0b11000000_00000000.toShort())
							break
						} else if (backRefLocation <= 0b00111111_11111111)
							backReferences[domain.substring(index + 1)] = backRefLocation
					}
				} else {
					label[length] = char.code.toByte()
					length++
				}
			}
		}

		message.questions.forEach { question ->
			writeDomain(question.domain)
			buffer.putShort(question.questionType.raw.toShort())
			buffer.putShort(question.questionClass.raw.toShort())
		}

		fun writeRR(rr: DNSResourceRecord) {
			writeDomain(rr.domain)
			buffer.putShort(rr.recordType.raw.toShort())
			buffer.putShort(rr.recordClass.raw.toShort())
			buffer.putInt(rr.timeToLive.inWholeSeconds.toInt())
			val savePosition = buffer.position()
			buffer.putShort(0)
			when (rr) {
				is DNSResourceRecord.IPv4Address -> buffer.put(rr.address)
				is DNSResourceRecord.AuthoritativeNameServer -> writeDomain(rr.nameServerDomainName)
				is DNSResourceRecord.CanonicalName -> writeDomain(rr.canonicalName)
				is DNSResourceRecord.StartOfAuthority -> {
					writeDomain(rr.nameServer)
					writeDomain(rr.responsibleMailbox)
					buffer.putInt(rr.serial.toInt())
					buffer.putInt(rr.refresh.inWholeSeconds.toInt())
					buffer.putInt(rr.retry.inWholeSeconds.toInt())
					buffer.putInt(rr.expire.inWholeSeconds.toInt())
					buffer.putInt(rr.minimumTTl.inWholeSeconds.toInt())
				}

				is DNSResourceRecord.MailExchange -> {
					buffer.putShort(rr.preference.toShort())
					writeDomain(rr.exchange)
				}

				is DNSResourceRecord.TextualData -> buffer.put(rr.text)
				is DNSResourceRecord.IPv6Address -> buffer.put(rr.address)
				is DNSResourceRecord.EDNSOpt -> {
					if (rr.options.isNotEmpty()) TODO("EDNS Options")
				}

				is DNSResourceRecord.Generic -> buffer.put(rr.data)
				else -> TODO(rr::class.qualifiedName.toString())
			}
			buffer.putShort(savePosition, (buffer.position() - savePosition - 2).toShort())
		}

		message.answerRecords.forEach(::writeRR)
		message.authorityRecords.forEach(::writeRR)
		message.additionalRecords.forEach(::writeRR)
	}

	fun readDomain(startPosition: Int, buffer: ByteBuffer): String {
		var label = ""
		while (true) {
			val length = buffer.get().toInt() and 0xFF
			when (val prefix = length ushr 6) {
				0b11 -> {
					val offset = (((length and 0b111111) shl 8) or (buffer.get().toInt() and 0xFF)) + startPosition
					val returnPosition = buffer.position()
					buffer.position(offset)
					label += readDomain(startPosition, buffer)
					buffer.position(returnPosition)
					return label
				}

				0b00 -> {
					if (length == 0) return label
					val labelBuffer = ByteArray(length)
					buffer.get(labelBuffer)
					label += String(labelBuffer, Charsets.ISO_8859_1) + '.'
				}

				else -> throw IllegalArgumentException("Unknown prefix $prefix")
			}
		}
	}

	fun readMessage(buffer: ByteBuffer): DNSMessage {
		val startPosition = buffer.position()
		val identifier = buffer.getShort()
		val flagBits = buffer.getShort().toUInt()
		val questions = buffer.getShort()
		val answers = buffer.getShort()
		val authorities = buffer.getShort()
		val additional = buffer.getShort()

		val responseCode = DNSResponseCode.entries.id(flagBits and 0b1111u)

		fun readResourceRecord(): DNSResourceRecord {
			val domain = readDomain(startPosition, buffer)
			val rType = DNSType.entries.id(buffer.getShort().toUShort())
			val rClass = DNSClass.entries.id(buffer.getShort().toUShort())
			val ttl = (buffer.getInt().toLong() and 0xFFFFFFFF).toDuration(DurationUnit.SECONDS)
			val dataLength = buffer.getShort().toInt() and 0xFFFF
			val decoded = when (rType.enum) {
				DNSType.A -> {
					if (dataLength != 4) throw IllegalArgumentException("IPv4 RR was $dataLength bytes, not 4")
					val v4Data = ByteArray(4)
					buffer.get(v4Data)
					DNSResourceRecord.IPv4Address(
						domain, rClass, ttl,
						v4Data
					)
				}

				DNSType.NS -> DNSResourceRecord.AuthoritativeNameServer(
					domain, rClass, ttl,
					readDomain(startPosition, buffer)
				)

				DNSType.CNAME -> DNSResourceRecord.CanonicalName(
					domain, rClass, ttl,
					readDomain(startPosition, buffer)
				)

				DNSType.SOA -> {
					val nsName = readDomain(startPosition, buffer)
					val rmName = readDomain(startPosition, buffer)
					val serial = buffer.getInt().toUInt()
					val refresh = (buffer.getInt().toLong() and 0xFFFFFFFF).toDuration(DurationUnit.SECONDS)
					val retry = (buffer.getInt().toLong() and 0xFFFFFFFF).toDuration(DurationUnit.SECONDS)
					val expire = (buffer.getInt().toLong() and 0xFFFFFFFF).toDuration(DurationUnit.SECONDS)
					val minimum = (buffer.getInt().toLong() and 0xFFFFFFFF).toDuration(DurationUnit.SECONDS)
					DNSResourceRecord.StartOfAuthority(
						domain, rClass, ttl,
						nsName, rmName,
						serial, refresh, retry, expire, minimum
					)
				}

				DNSType.MX -> {
					val pref = buffer.getShort()
					DNSResourceRecord.MailExchange(
						domain, rClass, ttl,
						pref.toUShort(), readDomain(startPosition, buffer)
					)
				}

				DNSType.TXT -> {
					val text = ByteArray(dataLength)
					buffer.get(text)
					DNSResourceRecord.TextualData(
						domain, rClass, ttl,
						text
					)
				}

				DNSType.AAAA -> {
					if (dataLength != 16) throw IllegalArgumentException("IPv6 RR was $dataLength bytes, not 16")
					val v6Data = ByteArray(16)
					buffer.get(v6Data)
					DNSResourceRecord.IPv6Address(
						domain, rClass, ttl,
						v6Data
					)
				}

				DNSType.OPT -> {
					val optTTLData = ttl.inWholeSeconds
					val options = mutableListOf<EDNS0Option>()
					var length = dataLength
					while (length > 0) {
						val oCode = EDNS0OptionValue.entries.id(buffer.getShort().toUShort())
						val oLength = buffer.getShort().toInt() and 0xFFFF
						length -= oLength + 4
						val data = ByteArray(oLength)
						buffer.get(data)
						options.add(
							EDNS0Option.Generic(oCode, data)
						)
					}
					DNSResourceRecord.EDNSOpt(
						rClass.raw,
						EDNS0ResponseCode.entries.id(
							((optTTLData ushr 24) shl 4).toUInt() or responseCode.raw
						),
						((optTTLData ushr 16) and 0xFF).toUByte(),
						((optTTLData ushr 15) and 1) == 1L,
						options
					)
				}

				else -> {
					logger.warning("Unknown type $rType!")
					val genericData = ByteArray(dataLength)
					buffer.get(genericData)
					DNSResourceRecord.Generic(
						domain, rType, rClass, ttl,
						genericData
					)
				}
			}
			return decoded
		}

		val q = MutableList(questions.toInt()) {
			DNSQuestion(
				readDomain(startPosition, buffer),
				DNSType.entries.id(buffer.getShort().toUShort()),
				DNSClass.entries.id(buffer.getShort().toUShort())
			)
		}
		val a = MutableList(answers.toInt()) { readResourceRecord() }
		val au = MutableList(authorities.toInt()) { readResourceRecord() }
		val ad = MutableList(additional.toInt()) { readResourceRecord() }

		return if (flagBits shr 15 != 0u) DNSMessage.Response(
			identifier,
			DNSOpcode.entries.id((flagBits shr 11) and 0b1111u),
			authoritative = (flagBits shr 10) and 1u == 1u,
			truncated = (flagBits shr 9) and 1u == 1u,
			recursionDesired = (flagBits shr 8) and 1u == 1u,
			recursionAvailable = (flagBits shr 7) and 1u == 1u,
			responseCode,
			q, a, au, ad
		) else DNSMessage.Request(
			identifier,
			DNSOpcode.entries.id((flagBits shr 11) and 0b1111u),
			truncated = (flagBits shr 9) and 1u == 1u,
			recursionDesired = (flagBits shr 8) and 1u == 1u,
			q, a, au, ad
		)
	}

	fun requestUDP(
		opcode: MappedEnumeration<UInt, DNSOpcode>,
		domain: String,
		dnsClass: MappedEnumeration<UShort, DNSClass>,
		dnsType: MappedEnumeration<UShort, DNSType>,
		resolveData: MemorySegment,
		resolveDataBuffer: ByteBuffer,
		address: ResolutionDataPartIdentifier
	): DNSMessage.Response {
		val socket = udpV6Sockets.openSocket()
		socket.get(IPv6SocketFeatures.CONFIGURE)
			.configure(WindowsIPv6SocketConfigurationFeatures.ALLOW_IPV6_AND_IPV4)
		socket.get(IPv6SocketFeatures.CONNECT).connect(
			when (address) {
				is InternetProtocolV4AddressPortData -> InternetProtocolV6AddressPortData(address)
				is InternetProtocolV6AddressPortData -> address
				else -> throw UnsupportedOperationException()
			}
		).block()
		resolveDataBuffer.clear()
		val sentIdentifier = Random.nextInt().toShort()
		var domainRandom = ""
		for (char in domain) domainRandom += if (char != '.' && Random.nextBoolean()) {
			if (char.isUpperCase()) char.lowercase() else char.uppercase()
		} else char
		writeMessage(
			resolveDataBuffer, DNSMessage.Request(
				sentIdentifier,
				opcode,
				truncated = false,
				recursionDesired = false,
				mutableListOf(
					DNSQuestion(
						domainRandom,
						dnsType,
						dnsClass
					)
				),
				additionalRecords = mutableListOf(
					DNSResourceRecord.EDNSOpt(
						UShort.MAX_VALUE,
						MappedEnumeration(0u),
						0u,
						false
					)
				)
			)
		)
		resolveDataBuffer.flip()
		// TODO: Ghost send bug
		socket.get(IPv6SocketFeatures.SEND).sendSegment(
			resolveData.reinterpret(resolveDataBuffer.limit().toLong())
		).block()
		resolveDataBuffer.clear()
		val receiveData = socket.get(IPv6SocketFeatures.RECEIVE).receiveSegment(resolveData).block()
		resolveDataBuffer.limit(receiveData.firstNotNullOf { it as? ReceiveSizeData }.bytes.toInt())
		socket.close(
			StandardCloseFeatures.STOP_TX,
			StandardCloseFeatures.STOP_RX,
			StandardCloseFeatures.RELEASE
		)
		val response = readMessage(resolveDataBuffer) as DNSMessage.Response
		if (response.identifier != sentIdentifier) TODO("WRONG IDENTIFIER")
		if (response.questions.first().domain != domainRandom) TODO("WRONG DOMAIN")
		return response
	}

	fun requestTCP(
		opcode: MappedEnumeration<UInt, DNSOpcode>,
		domain: String,
		dnsClass: MappedEnumeration<UShort, DNSClass>,
		dnsType: MappedEnumeration<UShort, DNSType>,
		resolveData: MemorySegment,
		resolveDataBuffer: ByteBuffer,
		address: ResolutionDataPartIdentifier
	): DNSMessage.Response {
		val socket = tcpV6Sockets.openSocket()
		socket.get(IPv6SocketFeatures.CONFIGURE)
			.configure(WindowsIPv6SocketConfigurationFeatures.ALLOW_IPV6_AND_IPV4)
		socket.get(IPv6SocketFeatures.CONNECT).connect(
			when (address) {
				is InternetProtocolV4AddressPortData -> InternetProtocolV6AddressPortData(address)
				is InternetProtocolV6AddressPortData -> address
				else -> throw UnsupportedOperationException()
			}
		).block()
		resolveDataBuffer.clear()
		val sentIdentifier = Random.nextInt().toShort()
		var domainRandom = ""
		for (char in domain) domainRandom += if (char != '.' && Random.nextBoolean()) {
			if (char.isUpperCase()) char.lowercase() else char.uppercase()
		} else char
		resolveDataBuffer.putShort(0)
		writeMessage(
			resolveDataBuffer, DNSMessage.Request(
				sentIdentifier,
				opcode,
				truncated = false,
				recursionDesired = false,
				mutableListOf(
					DNSQuestion(
						domainRandom,
						dnsType,
						dnsClass
					)
				)
			)
		)
		resolveDataBuffer.flip()
		resolveDataBuffer.putShort(0, (resolveDataBuffer.limit() - 2).toShort())
		socket.get(IPv6SocketFeatures.SEND).sendSegment(
			resolveData.reinterpret(resolveDataBuffer.limit().toLong())
		).block()
		resolveDataBuffer.clear()
		socket.get(IPv6SocketFeatures.RECEIVE).receiveSegment(resolveData).block()
		resolveDataBuffer.limit((resolveDataBuffer.getShort().toInt() and 0xFFFF) + 2)
		socket.close(
			StandardCloseFeatures.STOP_TX,
			StandardCloseFeatures.STOP_RX,
			StandardCloseFeatures.RELEASE
		)
		val response = readMessage(resolveDataBuffer) as DNSMessage.Response
		if (response.identifier != sentIdentifier) TODO("WRONG IDENTIFIER")
		if (response.questions.first().domain != domainRandom) TODO("WRONG DOMAIN")
		return response
	}

	fun request(
		opcode: MappedEnumeration<UInt, DNSOpcode>,
		domain: String,
		dnsClass: MappedEnumeration<UShort, DNSClass>,
		dnsType: MappedEnumeration<UShort, DNSType>,
		vararg addresses: ResolutionDataPartIdentifier
	): DNSMessage.Response {
		val resolveData = Arena.ofAuto().allocate(UShort.MAX_VALUE.toLong())
		val resolveDataBuffer = resolveData.asByteBuffer()
		// UDP
		val udp = requestUDP(
			opcode, domain, dnsClass, dnsType, resolveData, resolveDataBuffer,
			addresses.random()
		)
		if (!udp.truncated) return udp
		// TCP
		val tcp = requestTCP(
			opcode, domain, dnsClass, dnsType, resolveData, resolveDataBuffer,
			addresses.random()
		)
		return tcp
	}

	data class ServerCacheLeaf(
		var addresses: Array<out ResolutionDataPartIdentifier>,
		val labels: MutableMap<String, ServerCacheLeaf>
	) {
		override fun equals(other: Any?): Boolean {
			if (this === other) return true
			if (javaClass != other?.javaClass) return false

			other as ServerCacheLeaf

			if (!addresses.contentEquals(other.addresses)) return false
			if (labels != other.labels) return false

			return true
		}

		override fun hashCode(): Int {
			var result = addresses.contentHashCode()
			result = 31 * result + labels.hashCode()
			return result
		}
	}

	val serverCache = ServerCacheLeaf(
		arrayOf(
			InternetProtocolV4AddressPortData(
				byteArrayOf(198.toByte(), 41, 0, 4),
				53u
			),
			InternetProtocolV6AddressPortData(
				byteArrayOf(
					0x20, 0x01,
					0x05, 0x03,
					0xBA.toByte(), 0x3E,
					0x00, 0x00,
					0x00, 0x00,
					0x00, 0x00,
					0x00, 0x02,
					0x00, 0x30,
				),
				53u
			)
		),
		mutableMapOf()
	)

	fun findMostSpecificAddresses(
		domain: String
	): Array<out ResolutionDataPartIdentifier> {
		var leaf = serverCache.labels
		var lastSpecific = serverCache.addresses
		for (label in domain.removeSuffix(".").split('.').asReversed()) synchronized(leaf) {
			val cacheLeaf = leaf[label] ?: break
			lastSpecific = cacheLeaf.addresses
			leaf = cacheLeaf.labels
		}
		return lastSpecific
	}

	fun getServFail(opcode: MappedEnumeration<UInt, DNSOpcode>) = DNSMessage.Response(
		0x0,
		opcode,
		authoritative = false,
		truncated = false,
		true,
		recursionAvailable = true,
		MappedEnumeration(DNSResponseCode.ServFail)
	)

	fun resolve(
		opcode: MappedEnumeration<UInt, DNSOpcode>,
		domain: String,
		dnsClass: MappedEnumeration<UShort, DNSClass>,
		dnsType: MappedEnumeration<UShort, DNSType>,
		depth: Int = 0
	): DNSMessage.Response {
		lateinit var response: DNSMessage.Response
		var nextServers = findMostSpecificAddresses(domain)
		while (true) {
			if (depth > 30 || nextServers.isEmpty()) return getServFail(opcode)
			nextServers.shuffle()
			for (server in nextServers) {
				response = request(
					opcode,
					domain, dnsClass, dnsType,
					server
				)
				when (response.responseCode.enum) {
					DNSResponseCode.NoError, DNSResponseCode.NXDomain -> break
					else -> {}
				}
			}
			if (response.authoritative) {
				val cn = response.answerRecords.firstNotNullOfOrNull { it as? DNSResourceRecord.CanonicalName }
				if (cn != null && dnsType.enum != DNSType.CNAME) {
					val cnr = resolve(
						opcode,
						cn.canonicalName, dnsClass, dnsType,
						depth + 1
					)
					for (i in cnr.answerRecords.indices) {
						val answer = cnr.answerRecords[i]
						if (answer.domain == cn.canonicalName) cnr.answerRecords[i].domain = domain
					}
					cnr.additionalRecords.add(cn)
					return cnr
				} else break
			}
			val responsible = mutableMapOf<String, MutableList<ResolutionDataPartIdentifier>>()
			fun addResponsibleRR(ns: DNSResourceRecord.AuthoritativeNameServer, addr: DNSResourceRecord) {
				val addrDomainN = addr.domain.lowercase()
				if (ns.nameServerDomainName.lowercase() == addrDomainN) {
					val addressPort = when (addr) {
						is DNSResourceRecord.IPv4Address -> InternetProtocolV4AddressPortData(
							addr.address,
							53u
						)

						is DNSResourceRecord.IPv6Address -> InternetProtocolV6AddressPortData(
							addr.address,
							53u
						)

						else -> return
					}
					val nsDomainN = ns.domain.lowercase()
					var addresses = responsible[nsDomainN]
					if (addresses == null) {
						addresses = mutableListOf()
						responsible[nsDomainN] = addresses
					}
					addresses.add(addressPort)
				}
			}
			response.authorityRecords.forEach auth@{ authority ->
				if (authority !is DNSResourceRecord.AuthoritativeNameServer) return@auth
				response.additionalRecords.forEach { additional -> addResponsibleRR(authority, additional) }
				if (responsible.isEmpty()) resolve(
					opcode,
					authority.nameServerDomainName, dnsClass, MappedEnumeration(DNSType.A),
					depth + 1
				).answerRecords.forEach { answer -> addResponsibleRR(authority, answer) }
				if (responsible.isEmpty()) resolve(
					opcode,
					authority.nameServerDomainName, dnsClass, MappedEnumeration(DNSType.AAAA),
					depth + 1
				).answerRecords.forEach { answer -> addResponsibleRR(authority, answer) }
			}
			if (responsible.isEmpty()) return getServFail(opcode)
			responsible.forEach {
				var leaf = serverCache
				val labels = it.key.removeSuffix(".").split('.').asReversed()
				labels.forEachIndexed { i, label ->
					var extantLeaf = leaf.labels[label]
					if (extantLeaf == null) {
						extantLeaf = ServerCacheLeaf(emptyArray(), mutableMapOf())
						leaf.labels[label] = extantLeaf
					}
					if (i != labels.lastIndex) leaf = extantLeaf
					else extantLeaf.addresses = it.value.toTypedArray()
				}
			}
			nextServers = responsible.flatMap { it.value }.toTypedArray()
		}
		return response
	}

	fun processMessage(message: DNSMessage.Request) = if (message.questions.size != 1)
		DNSMessage.Response(
			message.identifier,
			message.opcode,
			authoritative = false,
			truncated = false,
			message.recursionDesired,
			recursionAvailable = true,
			MappedEnumeration(DNSResponseCode.FormErr)
		)
	else {
		val question = message.questions.first()
		val data = if (message.recursionDesired) resolve(
			message.opcode,
			question.domain, question.questionClass, question.questionType
		) else request(
			message.opcode,
			question.domain, question.questionClass, question.questionType,
			*serverCache.addresses
		)
		data.additionalRecords.removeIf { it is DNSResourceRecord.EDNSOpt }
		DNSMessage.Response(
			message,
			data.authoritative,
			truncated = false,
			recursionAvailable = true,
			data.responseCode,
			data.answerRecords,
			data.authorityRecords,
			data.additionalRecords
		)
	}

	fun <T : ResolutionDataPartIdentifier> processTCP(
		socketBind: () -> BSLSocket<T>
	) {
		val serverSocket = socketBind()

		while (true) {
			val acceptData = serverSocket.get(IPv6SocketFeatures.ACCEPT)
				.accept()
				.block()
			val acceptedTx = acceptData.firstNotNullOf { it as? InternetProtocolV6AddressPortData }
			val acceptedSocket = acceptData.firstNotNullOf { it as? BSLSocket<*> }
			Thread.ofPlatform().start {
				logger.info("TCP ... $acceptedTx")
				val requestData = Arena.ofAuto().allocate(UShort.MAX_VALUE.toLong() + 2)
				val requestDataBuffer = requestData.asByteBuffer()
				while (true) {
					requestDataBuffer.clear()
					val receiveData = acceptedSocket.get(IPv6SocketFeatures.RECEIVE)
						.receiveSegment(requestData)
						.block()
					val receiveSize = receiveData.firstNotNullOf { it as? ReceiveSizeData }.bytes
					if (receiveSize == 0L) break
					requestDataBuffer.limit((requestDataBuffer.getShort().toInt() and 0xFFFF) + 2)
					val message = readMessage(requestDataBuffer)
					if (message !is DNSMessage.Request) break
					requestDataBuffer.clear()
					requestDataBuffer.putShort(0)
					val sendMessage: DNSMessage = processMessage(message)
					writeMessage(requestDataBuffer, sendMessage)
					requestDataBuffer.flip()
					val msgSize = requestDataBuffer.limit() - 2
					if (msgSize > UShort.MAX_VALUE.toInt()) {
						requestDataBuffer.putShort(
							4,
							requestDataBuffer.getShort(4) or (1 shl 9).toShort() // truncated
						)
						requestDataBuffer.limit(UShort.MAX_VALUE.toInt())
					}
					requestDataBuffer.putShort(0, msgSize.toShort())
					acceptedSocket.get(IPv6SocketFeatures.SEND).sendSegment(
						requestData.reinterpret(requestDataBuffer.limit().toLong())
					).block()
				}
				acceptedSocket.close(
					StandardCloseFeatures.STOP_TX, StandardCloseFeatures.STOP_RX,
					StandardCloseFeatures.RELEASE
				)
			}
		}
	}

	fun <T : ResolutionDataPartIdentifier> processUDP(
		socketBind: () -> BSLSocket<T>
	) {
		val requestData = Arena.ofAuto().allocate(UShort.MAX_VALUE.toLong())
		val requestDataBuffer = requestData.asByteBuffer()

		var serverSocket = socketBind()
		while (true) {
			requestDataBuffer.clear()
			val receiveData = try {
				serverSocket.get(IPv6SocketFeatures.RECEIVE).receiveSegment(requestData)
			} catch (_: WindowsLastErrorException) {
				serverSocket.close(StandardCloseFeatures.RELEASE)
				serverSocket = socketBind()
				serverSocket.get(IPv6SocketFeatures.RECEIVE).receiveSegment(requestData)
			}.block()
			requestDataBuffer.limit(receiveData.firstNotNullOf { it as? ReceiveSizeData }.bytes.toInt())
			val receiveSrc = receiveData.firstNotNullOf { it as? InternetProtocolV6AddressPortData }
			logger.info("UDP ... $receiveSrc")
			val message = readMessage(requestDataBuffer)
			if (message !is DNSMessage.Request) continue
			val eDNS = message.additionalRecords.firstNotNullOfOrNull { it as? DNSResourceRecord.EDNSOpt }
			requestDataBuffer.clear()
			val sendMessage: DNSMessage = processMessage(message)
			val sizeLimit = if (eDNS == null) 512
			else {
				sendMessage.additionalRecords.add(
					DNSResourceRecord.EDNSOpt(
						eDNS.udpPayloadSize,
						MappedEnumeration(0u),
						0u,
						false
					)
				)
				eDNS.udpPayloadSize.toInt()
			}
			writeMessage(requestDataBuffer, sendMessage)
			requestDataBuffer.flip()
			if (requestDataBuffer.limit() > sizeLimit) {
				requestDataBuffer.putShort(
					2,
					requestDataBuffer.getShort(2) or (1 shl 9).toShort() // truncated
				)
				requestDataBuffer.limit(sizeLimit)
			}
			serverSocket.get(IPv6SocketFeatures.SEND).sendSegment(
				requestData.reinterpret(requestDataBuffer.limit().toLong()),
				receiveSrc
			).block()
		}
	}

	Thread.ofPlatform().name("TCP").start {
		processTCP {
			val tcpV6Resolution = tcpV6.get(IPv6TCPFeatures.NAME_RESOLUTION)
			val tcpV6RecvAny = tcpV6Resolution.resolve(
				"", 53u,
				WindowsResolutionFeatures.PASSIVE
			).firstNotNullOf {
				it as? ResolutionDataPart
			}.data.firstNotNullOf { it as? InternetProtocolV6AddressData }

			val socket = tcpV6Sockets.openSocket()
			socket.get(IPv6SocketFeatures.CONFIGURE)
				.configure(WindowsIPv6SocketConfigurationFeatures.ALLOW_IPV6_AND_IPV4)
			socket.get(IPv6SocketFeatures.BIND)
				.bind(InternetProtocolV6AddressPortData(tcpV6RecvAny.data, 53u))
			socket.get(IPv6SocketFeatures.LISTEN)
				.listen()
			socket
		}
	}

	Thread.ofPlatform().name("UDP").start {
		processUDP {
			val udpV6Resolution = udpV6.get(IPv6UDPFeatures.NAME_RESOLUTION)
			val udpV6RecvAny = udpV6Resolution.resolve(
				"", 53u,
				WindowsResolutionFeatures.PASSIVE
			).firstNotNullOf {
				it as? ResolutionDataPart
			}.data.firstNotNullOf { it as? InternetProtocolV6AddressData }

			val socket = udpV6Sockets.openSocket()
			socket.get(IPv6SocketFeatures.CONFIGURE)
				.configure(WindowsIPv6SocketConfigurationFeatures.ALLOW_IPV6_AND_IPV4)
			socket.get(IPv6SocketFeatures.BIND)
				.bind(InternetProtocolV6AddressPortData(udpV6RecvAny.data, 53u))
			socket
		}
	}
}