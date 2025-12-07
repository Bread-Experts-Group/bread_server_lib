package org.bread_experts_group.api.system.socket.receive

import org.bread_experts_group.api.system.socket.ipv4.receive.IPv4ReceiveDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.receive.IPv6ReceiveDataIdentifier

data class ReceiveSizeData(val bytes: Long) : IPv4ReceiveDataIdentifier, IPv6ReceiveDataIdentifier {
	constructor(uBytes: Int) : this(uBytes.toLong() and 0xFFFFFFFF)
}