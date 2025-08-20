package org.bread_experts_group.protocol.ip.v4

import org.bread_experts_group.coder.Mappable

enum class InternetProtocolV4Transport(
	override val id: UByte,
	override val tag: String
) : Mappable<InternetProtocolV4Transport, UByte> {
	ICMP(1u, "Internet Control Message Protocol (ICMP)"),
	TCP(6u, "Transmission Control Protocol (TCP)"),
	UDP(17u, "User Datagram Protocol (UDP)");

	override fun toString(): String = stringForm()
}