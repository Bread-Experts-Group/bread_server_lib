package org.bread_experts_group.socket.protocol

import org.bread_experts_group.channel.ReadingByteBuffer
import org.bread_experts_group.channel.byteInt
import org.bread_experts_group.channel.shortInt
import org.bread_experts_group.coder.Flaggable.Companion.from
import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.coder.MappedEnumeration
import java.net.Inet4Address
import java.nio.ByteBuffer
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
	val data: ByteArray
) {
	override fun toString(): String = "$source -> $destination [#x${identification.toHexString(HexFormat.UpperCase)}]" +
			"\n\t$timeToLive max hops, containing $protocol, #${data.size} bytes" +
			"\n\t$flags" +
			(if (!flags.contains(InternetProtocolV4Flags.DONT_FRAGMENT)) ", fragment # [$fragmentOffset]" else "")

	companion object {
		fun decode(from: ReadingByteBuffer): InternetProtocolV4 {
			val initial = from.u8i32()
			(initial shr 4).let { if (it != 4) throw ProtocolDecodingException("Incorrect IPv4 header version [$it]") }
			val data = ByteArray(((initial and 0xF) * 4) - 1)
			from.get(data)
			return this.decodeHeader(ByteBuffer.wrap(data), from)
		}

		private fun decodeHeader(from: ByteBuffer, readable: ReadingByteBuffer): InternetProtocolV4 {
			val o2 = from.byteInt
			val differentiatedServicesCodePoint = (o2 shr 2).toUByte()
			val explicitCongestionNotification = (o2 and 0b11).toUByte()
			val totalLength = from.shortInt
			if (totalLength < (from.capacity() - 1)) throw ProtocolDecodingException("Total Length is invalid")
			val identification = from.short.toUShort()
			val o6a7 = from.shortInt
			val flags = InternetProtocolV4Flags.entries.from(o6a7 shr 13)
			val fragmentOffset = (o6a7 and 0x1FFF).toUShort()
			val timeToLive = from.get().toUByte()
			val protocol = InternetProtocolV4Transport.entries.id(from.get().toUByte())
			val checksum = from.short.toUShort()
			val source = ByteArray(4)
			val destination = ByteArray(4)
			from.get(source)
			from.get(destination)
			val data = ByteArray(totalLength - from.capacity() - 1)
			readable.get(data)
			return InternetProtocolV4(
				differentiatedServicesCodePoint, explicitCongestionNotification,
				identification, flags, fragmentOffset, timeToLive, protocol,
				Inet4Address.getByAddress(source) as Inet4Address,
				Inet4Address.getByAddress(destination) as Inet4Address,
				data
			)
		}
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