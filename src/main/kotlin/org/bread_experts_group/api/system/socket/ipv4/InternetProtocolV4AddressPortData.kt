package org.bread_experts_group.api.system.socket.ipv4

import org.bread_experts_group.api.system.socket.ipv4.accept.IPv4AcceptDataIdentifier
import org.bread_experts_group.api.system.socket.ipv4.connect.IPv4ConnectionFeatureIdentifier
import org.bread_experts_group.api.system.socket.ipv4.receive.IPv4ReceiveDataIdentifier
import org.bread_experts_group.api.system.socket.ipv4.send.IPv4SendFeatureIdentifier

open class InternetProtocolV4AddressPortData(
	data: ByteArray,
	val port: UShort
) : InternetProtocolV4AddressData(data), IPv4ConnectionFeatureIdentifier, IPv4AcceptDataIdentifier,
	IPv4ReceiveDataIdentifier, IPv4SendFeatureIdentifier {
	override fun toString(): String = "IPv4[${data.joinToString(".") { it.toUByte().toString() }}:$port]"
}