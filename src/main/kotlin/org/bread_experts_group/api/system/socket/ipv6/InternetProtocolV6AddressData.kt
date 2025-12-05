package org.bread_experts_group.api.system.socket.ipv6

import org.bread_experts_group.api.system.socket.ipv6.accept.IPv6AcceptFeatureIdentifier
import org.bread_experts_group.api.system.socket.ipv6.bind.IPv6BindFeatureIdentifier
import org.bread_experts_group.api.system.socket.resolution.ResolutionDataPartIdentifier

open class InternetProtocolV6AddressData(
	val data: ByteArray
) : ResolutionDataPartIdentifier, IPv6AcceptFeatureIdentifier, IPv6BindFeatureIdentifier {
	companion object {
		fun v4Tov6Bytes(v4: ByteArray) = byteArrayOf(
			0x00, 0x00,
			0x00, 0x00,
			0x00, 0x00,
			0x00, 0x00,
			0x00, 0x00,
			0xFF.toByte(), 0xFF.toByte(),
			v4[0], v4[1],
			v4[2], v4[3]
		)
	}

	protected fun collect(): String {
		var string = ""
		var i = 0
		while (i < data.size) {
			val short = ((data[i++].toInt() and 0xFF) shl 8) or (data[i++].toInt() and 0xFF)
			string += short.toUShort().toHexString()
			if (i != data.size) string += ':'
		}
		return string
	}

	override fun toString(): String = "IPv6[${collect()}]"
}