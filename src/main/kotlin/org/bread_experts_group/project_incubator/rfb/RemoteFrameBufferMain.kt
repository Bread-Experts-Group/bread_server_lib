package org.bread_experts_group.project_incubator.rfb

import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.api.system.socket.BSLSocketConnectionEnded
import org.bread_experts_group.api.system.socket.SystemSocketProviderFeatures
import org.bread_experts_group.api.system.socket.close.StandardCloseFeatures
import org.bread_experts_group.api.system.socket.ipv6.*
import org.bread_experts_group.api.system.socket.ipv6.config.WindowsIPv6SocketConfigurationFeatures
import org.bread_experts_group.api.system.socket.ipv6.stream.SystemInternetProtocolV6StreamProtocolFeatures
import org.bread_experts_group.api.system.socket.ipv6.stream.tcp.IPv6TCPFeatures
import org.bread_experts_group.api.system.socket.resolution.ResolutionDataPart
import org.bread_experts_group.api.system.socket.resolution.StandardResolutionFeatures
import org.bread_experts_group.io.reader.BSLSocketReader
import org.bread_experts_group.io.reader.BSLSocketWriter
import org.bread_experts_group.project_incubator.rfb.PixelFormatDataStructure.Companion.nextPixelFormatStructure
import org.bread_experts_group.project_incubator.rfb.PixelFormatDataStructure.Companion.writePixelFormatStructure
import java.nio.ByteOrder
import java.util.*
import kotlin.random.Random

fun client(
	read: BSLSocketReader<*, *>, write: BSLSocketWriter<*, *>
) {
	fun shutdown(reason: String) {
		println("Shutting down: $reason")
	}

	// 7.1.1 ProtocolVersion Handshake
	write.write("RFB 003.008\n".toByteArray(Charsets.US_ASCII))
	write.flush()
	val clientPV = read.readN(12).toString(Charsets.US_ASCII).let {
		if (it.last() != '\n') return shutdown("Protocol version did not end with line feed \"$it\"")
		it.take(it.length - 1)
	}
	val delimiter = clientPV.indexOf(' ')
	if (
		delimiter == -1 ||
		clientPV.take(delimiter) != "RFB"
	) return shutdown("Bad header presented in protocol version \"$clientPV\"")
	val clientVersion = clientPV.substring(delimiter + 1)
		.split('.', limit = 2)
		.mapNotNull { it.toIntOrNull() }
	if (clientVersion.size != 2) return shutdown("Bad protocol version format \"$clientPV\"")
	if (clientVersion[0] != 3) return shutdown("Protocol version too high \"$clientPV\"")
	val minorVersionInUse = when (val minor = clientVersion[1]) {
		3, 7, 8 -> minor
		else -> 3
	}
	println("Client version... ${clientVersion[0]}.${clientVersion[1]} (using minor version $minorVersionInUse)")

	// 7.1.2 Security Handshake
	write.write8i(1)
	write.write8i(1)
	write.flush()
	if (read.readU8i() != 1) return shutdown("Client did not agree to the provided security methods")

	// 7.1.3 SecurityResult Handshake
	write.write32(0)
	write.flush()

	// 7.3.1 ClientInit
	if (read.readU8i() == 0) println("Client wants desktop exclusive access")
	else println("Client is OK with desktop shared access")

	// 7.3.2 ServerInit (7.4 Pixel Format Data Structure)
	write.write16i(256) // W
	write.write16i(64) // H
	var currentFormat = PixelFormatDataStructure(
		32, 24,
		bigEndian = true,
		trueColor = true,
		255, 255, 255,
		24, 16, 8
	)
	writePixelFormatStructure(write, currentFormat)
	write.write32(16)
	write.write("TestString16....".toByteArray(Charsets.US_ASCII))
	write.flush()

	val encodings = EnumSet.noneOf(RemoteFramebufferEncodings::class.java)
	// 7.5 Client-to-Server Messages
	while (true) {
		when (val type = read.readU8i()) {
			// 7.5.1 SetPixelFormat
			0 -> {
				read.skip(3)
				currentFormat = nextPixelFormatStructure(read)
			}

			// SetEncodings
			2 -> {
				read.skip(1)
				repeat(read.readU16i()) {
					when (val encoding = read.readS32()) {
						0 -> encodings.add(RemoteFramebufferEncodings.RAW)
						1 -> encodings.add(RemoteFramebufferEncodings.COPYRECT)
						2 -> encodings.add(RemoteFramebufferEncodings.RRE)
						5 -> encodings.add(RemoteFramebufferEncodings.HEXTILE)
						15 -> encodings.add(RemoteFramebufferEncodings.TRLE)
						16 -> encodings.add(RemoteFramebufferEncodings.ZRLE)
						-239 -> encodings.add(RemoteFramebufferEncodings.CURSOR_PSEUDO_ENCODING)
						-223 -> encodings.add(RemoteFramebufferEncodings.DESKTOP_SIZE_PSEUDO_ENCODING)
						else -> println("Unsupported encoding $encoding")
					}
				}
			}

			// 7.5.3 / 7.6.1 FramebufferUpdateRequest / FramebufferUpdate
			3 -> {
				val incremental = read.readU8i()
				val x = read.readU16i()
				val y = read.readU16i()
				val w = read.readU16i()
				val h = read.readU16i()
				write.write8i(0)
				write.fill(1)
				write.write16i(1)
				// Rect
				write.write16i(x)
				write.write16i(y)
				write.write16i(w)
				write.write16i(h)
				write.write32(0) // RAW encoding
				for (y in y until (y + h)) {
					for (x in x until (x + w)) {
						val rgb = Random.nextInt()
						val r = (rgb shr 16) and 0xFF
						val g = (rgb shr 8) and 0xFF
						val b = rgb and 0xFF
						var p = (r shl currentFormat.redShift) or
								(g shl currentFormat.greenShift) or
								(b shl currentFormat.blueShift)
						if (!currentFormat.bigEndian) p = Integer.reverseBytes(p)
						when (currentFormat.bitsPerPixel) {
							8 -> write.write8i(p)
							16 -> write.write16i(p)
							32 -> write.write32l(p.toLong())
							else -> return shutdown("Client was corrupted $currentFormat")
						}
					}
				}
				write.flush()
			}

			// KeyEvent
			4 -> {
				println("Down ${read.readU8i()}")
				read.skip(2)
				println("Key ${read.readS32()}")
				write.write8i(2)
				write.flush()
			}

			// PointerEvent
			5 -> {
				println("Button Mask ${read.readU8i()}")
				println("X ${read.readU16i()}")
				println("Y ${read.readU16i()}")
			}

			6 -> {
				println("Client cut text")
				read.skip(3)
				println(read.readN(read.readS32()).toString(Charsets.ISO_8859_1))
			}

			else -> return shutdown("Client sent unknown message type ... $type")
		}
	}
}

fun main() {
	val tcpV6 = SystemProvider.get(SystemFeatures.NETWORKING_SOCKETS)
		.get(SystemSocketProviderFeatures.INTERNET_PROTOCOL_V6)
		.get(SystemInternetProtocolV6SocketProviderFeatures.STREAM_PROTOCOLS)
		.get(SystemInternetProtocolV6StreamProtocolFeatures.TRANSMISSION_CONTROL_PROTOCOL)
	val tcpV6Resolution = tcpV6.get(IPv6TCPFeatures.NAME_RESOLUTION)
	val tcpV6RecvAny = tcpV6Resolution.resolve(
		"", 5900u,
		StandardResolutionFeatures.PASSIVE
	).firstNotNullOf {
		it as? ResolutionDataPart
	}.data.firstNotNullOf { it as? InternetProtocolV6AddressData }
	val tcpV6Socket = tcpV6.get(IPv6TCPFeatures.SOCKET)
		.openSocket()
	tcpV6Socket.get(IPv6SocketFeatures.CONFIGURE)
		.configure(WindowsIPv6SocketConfigurationFeatures.ALLOW_IPV6_AND_IPV4)
	tcpV6Socket.get(IPv6SocketFeatures.BIND)
		.bind(InternetProtocolV6AddressPortData(tcpV6RecvAny.data, 5900u))
	tcpV6Socket.get(IPv6SocketFeatures.LISTEN)
		.listen()
	// PROTOTYPE LOGIC
	while (true) {
		val acceptData = tcpV6Socket.get(IPv6SocketFeatures.ACCEPT)
			.accept()
			.block()
		val acceptedAddress = acceptData.firstNotNullOf { it as? InternetProtocolV6AddressPortData }
		val acceptedSocket = acceptData.firstNotNullOf { it as? IPv6Socket }

		Thread.ofVirtual().name("VNC Session $acceptedAddress").start {
			val read = BSLSocketReader(acceptedSocket.get(IPv6SocketFeatures.RECEIVE))
			val write = BSLSocketWriter(acceptedSocket.get(IPv6SocketFeatures.SEND))
			read.order = ByteOrder.BIG_ENDIAN
			write.order = ByteOrder.BIG_ENDIAN
			try {
				client(read, write)
			} catch (_: BSLSocketConnectionEnded) {
			}
			acceptedSocket.close(
				StandardCloseFeatures.STOP_RX, StandardCloseFeatures.STOP_TX,
				StandardCloseFeatures.RELEASE
			)
			println("Ending thread ${Thread.currentThread()}!")
		}
	}
}