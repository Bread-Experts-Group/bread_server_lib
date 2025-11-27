package org.bread_experts_group.api.system.socket.ipv4

import org.bread_experts_group.api.system.socket.resolution.ResolutionDataPartIdentifier

open class InternetProtocolV4AddressData(
	val data: ByteArray
) : ResolutionDataPartIdentifier {
	override fun toString(): String = "IPv4[${data.joinToString(".") { it.toUByte().toString() }}]"
}