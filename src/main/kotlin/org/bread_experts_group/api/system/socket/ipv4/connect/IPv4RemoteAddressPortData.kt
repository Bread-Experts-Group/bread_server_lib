package org.bread_experts_group.api.system.socket.ipv4.connect

import org.bread_experts_group.api.system.socket.ipv4.InternetProtocolV4AddressPortData

class IPv4RemoteAddressPortData(
	data: ByteArray,
	port: UShort
) : InternetProtocolV4AddressPortData(data, port) {
	override fun toString(): String = super.toString() + "/TCP/Remote"
}