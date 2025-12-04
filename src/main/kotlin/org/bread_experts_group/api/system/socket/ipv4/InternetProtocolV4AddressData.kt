package org.bread_experts_group.api.system.socket.ipv4

import org.bread_experts_group.api.system.socket.ipv4.accept.IPv4AcceptFeatureIdentifier
import org.bread_experts_group.api.system.socket.ipv4.bind.IPv4BindFeatureIdentifier
import org.bread_experts_group.api.system.socket.resolution.ResolutionDataPartIdentifier

open class InternetProtocolV4AddressData(
	val data: ByteArray
) : ResolutionDataPartIdentifier, IPv4AcceptFeatureIdentifier, IPv4BindFeatureIdentifier {
	override fun toString(): String = "IPv4[${data.joinToString(".") { it.toUByte().toString() }}]"
}