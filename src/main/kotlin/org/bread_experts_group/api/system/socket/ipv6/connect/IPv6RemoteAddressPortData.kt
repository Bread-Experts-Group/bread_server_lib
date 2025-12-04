package org.bread_experts_group.api.system.socket.ipv6.connect

import org.bread_experts_group.api.system.socket.ipv6.InternetProtocolV6AddressPortData

class IPv6RemoteAddressPortData(
	data: ByteArray,
	port: UShort
) : InternetProtocolV6AddressPortData(data, port) {
	override fun toString(): String = super.toString() + "/TCP/Remote"
}