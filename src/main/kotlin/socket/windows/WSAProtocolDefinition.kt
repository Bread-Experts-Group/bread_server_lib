package org.bread_experts_group.socket.windows

import org.bread_experts_group.coder.Mappable

enum class WSAProtocolDefinition(
	override val id: Int,
	override val tag: String
) : Mappable<WSAProtocolDefinition, Int> {
	IPPROTO_IP(0, "Internet Protocol (IP)"),
	IPPROTO_ICMP(1, "Internet Control Message Protocol (ICMP)"),
	IPPROTO_IGMP(2, "Internet Group Management Protocol (IGMP)"),
	BTHPROTO_RFCOMM(3, "Bluetooth Radio Frequency Communications (RFCOMM)"),
	IPPROTO_TCP(6, "Transmission Control Protocol (TCP)"),
	IPPROTO_UDP(17, "User Datagram Protocol (UDP)"),
	IPPROTO_ICMPV6(58, "Internet Control Message Protocol, version 6 (ICMPv6)"),
	IPPROTO_RM(113, "PGM Reliable Multicast Protocol");

	override fun toString(): String = stringForm()
}