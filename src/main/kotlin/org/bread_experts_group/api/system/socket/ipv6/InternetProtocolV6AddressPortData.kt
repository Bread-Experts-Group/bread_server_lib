package org.bread_experts_group.api.system.socket.ipv6

import org.bread_experts_group.api.system.socket.ipv4.InternetProtocolV4AddressPortData
import org.bread_experts_group.api.system.socket.ipv6.accept.IPv6AcceptDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.connect.IPv6ConnectionFeatureIdentifier
import org.bread_experts_group.api.system.socket.ipv6.receive.IPv6ReceiveDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.send.IPv6SendFeatureIdentifier

open class InternetProtocolV6AddressPortData(
	data: ByteArray,
	val port: UShort
) : InternetProtocolV6AddressData(data), IPv6ConnectionFeatureIdentifier, IPv6AcceptDataIdentifier,
	IPv6ReceiveDataIdentifier, IPv6SendFeatureIdentifier {
	constructor(v4: InternetProtocolV4AddressPortData) : this(
		v4Tov6Bytes(v4.data),
		v4.port
	)

	override fun toString(): String = "IPv6[${collect()}:$port]"
}