package org.bread_experts_group.protocol.ip.v4

import org.bread_experts_group.coder.Flaggable.Companion.from
import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.coder.MappedEnumeration
import org.bread_experts_group.computer.BinaryUtil.shr
import org.bread_experts_group.io.*
import org.bread_experts_group.protocol.ip.InternetProtocol
import java.net.Inet4Address
import java.util.EnumSet
import kotlin.text.HexFormat

data class InternetProtocolV4(
	val differentiatedServicesCodePoint: UByte,
	val explicitCongestionNotification: UByte,
	val identification: UShort,
	val flags: EnumSet<InternetProtocolV4Flags>,
	val fragmentOffset: UShort,
	val timeToLive: UByte,
	val protocol: MappedEnumeration<UByte, InternetProtocolV4Transport>,
	val source: Inet4Address,
	val destination: Inet4Address,
	val options: List<InternetProtocolV4Option>,
	val data: ByteArray
) : InternetProtocol() {
	override fun toString(): String = "$source -> $destination [#x${identification.toHexString(HexFormat.UpperCase)}]" +
			"\n\t$timeToLive max hops, containing $protocol, # bytes ${data.size}" +
			"\n\t$flags" +
			(if (!flags.contains(InternetProtocolV4Flags.DONT_FRAGMENT)) ", fragment # [$fragmentOffset]" else "") +
			", # options ${options.size}" + options.joinToString {
		"\n\t\t${it.type}: # bytes ${it.data.size}"
	}

	companion object {
		fun read(
			ihl: UByte,
			o2: UByte,
			totalLength: UShort,
			identification: UShort,
			flagsFOffset: UShort,
			ttl: UByte,
			protocol: MappedEnumeration<UByte, InternetProtocolV4Transport>,
			checksum: UShort,
			source: Inet4Address,
			destination: Inet4Address,
			from: BaseReadingIO
		): InternetProtocolV4 {
			val ihl = (ihl and 0b1111u) * 4u
			val dscp = o2 shr 2
			val ecn = o2 and 11u
			val flags = (flagsFOffset shr 13).toUByte()
			val offset = flagsFOffset and 0b00011111_11111111u
			val (data, options) = if (totalLength < ihl) {
				from.invalidateData()
				byteArrayOf() to emptyList<InternetProtocolV4Option>()
			} else {
				val options = mutableListOf<InternetProtocolV4Option>()
				var ihlRemainder = ihl - 20u
				while (ihlRemainder > 0u) {
					val type = InternetProtocolV4OptionType.entries.id(from.u8())
					ihlRemainder -= 1u
					if (type.enum == InternetProtocolV4OptionType.NO_OPERATION) continue
					if (type.enum == InternetProtocolV4OptionType.END_OF_OPTIONS_LIST) break
					val data = from.get((from.u8().toInt() - 2))
					ihlRemainder -= (data.size + 1).toUByte()
					options.add(InternetProtocolV4Option(type, data))
				}
				from.get((totalLength - ihl).toInt()) to options
			}
			return InternetProtocolV4(
				dscp, ecn, identification,
				InternetProtocolV4Flags.entries.from(flags), offset, ttl, protocol,
				source, destination, options, data
			)
		}

		val ipv4AddrLayout: PrimitiveIOLayout<Inet4Address> = PrimitiveIOLayout(
			{ r -> Inet4Address.getByAddress(r.get(4)) as Inet4Address },
			{ w, i -> w.put(i.address) }
		)

		val layout = SequentialIOLayout(
			::read,
			IOLayout.UNSIGNED_BYTE.passedUpwards(), // version / ihl
			IOLayout.UNSIGNED_BYTE, // dscp / ecn
			IOLayout.UNSIGNED_SHORT.order(IOEndian.BIG), // total length
			IOLayout.UNSIGNED_SHORT.order(IOEndian.BIG), // identification
			IOLayout.UNSIGNED_SHORT.order(IOEndian.BIG), // flags / fragment offset
			IOLayout.UNSIGNED_BYTE, // time to live
			IOLayout.enum(InternetProtocolV4Transport.entries, IOLayout.UNSIGNED_BYTE), // protocol
			IOLayout.UNSIGNED_SHORT.order(IOEndian.BIG), // checksum
			ipv4AddrLayout, // source address
			ipv4AddrLayout, // destination address
		)
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as InternetProtocolV4

		if (differentiatedServicesCodePoint != other.differentiatedServicesCodePoint) return false
		if (explicitCongestionNotification != other.explicitCongestionNotification) return false
		if (identification != other.identification) return false
		if (flags != other.flags) return false
		if (fragmentOffset != other.fragmentOffset) return false
		if (timeToLive != other.timeToLive) return false
		if (protocol != other.protocol) return false
		if (source != other.source) return false
		if (destination != other.destination) return false
		if (!data.contentEquals(other.data)) return false

		return true
	}

	override fun hashCode(): Int {
		var result = differentiatedServicesCodePoint.hashCode()
		result = 31 * result + explicitCongestionNotification.hashCode()
		result = 31 * result + identification.hashCode()
		result = 31 * result + flags.hashCode()
		result = 31 * result + fragmentOffset.hashCode()
		result = 31 * result + timeToLive.hashCode()
		result = 31 * result + protocol.hashCode()
		result = 31 * result + source.hashCode()
		result = 31 * result + destination.hashCode()
		result = 31 * result + data.contentHashCode()
		return result
	}
}