package org.bread_experts_group.socket.protocol

import org.bread_experts_group.coder.Mappable

enum class InternetProtocolV4Transport(
	override val id: UByte,
	override val tag: String
) : Mappable<InternetProtocolV4Transport, UByte> {
	ICMP(1u, "Internet Control Message Protocol (ICMP)"),
	TCP(6u, "Transmission Control Protocol (TCP)"),
	UDP(17u, "User Datagram Protocol (UDP)"),
	OTHER(255u, "Other");

	override fun other(): InternetProtocolV4Transport? = OTHER
	override fun toString(): String = stringForm()
}