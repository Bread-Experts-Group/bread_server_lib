package org.bread_experts_group.api.system.socket.ipv4

import org.bread_experts_group.api.system.socket.connect.IPv4TCPConnectionFeatureIdentifier

open class InternetProtocolV4AddressPortData(
	data: ByteArray,
	val port: UShort
) : InternetProtocolV4AddressData(data), IPv4TCPConnectionFeatureIdentifier {
	override fun toString(): String = "IPv4[${data.joinToString(".") { it.toUByte().toString() }}:$port]"
}