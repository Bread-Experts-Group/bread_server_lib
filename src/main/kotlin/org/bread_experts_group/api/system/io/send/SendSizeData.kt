package org.bread_experts_group.api.system.io.send

import org.bread_experts_group.api.system.socket.ipv4.send.IPv4SendDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.send.IPv6SendDataIdentifier

data class SendSizeData(val bytes: Long) : IPv4SendDataIdentifier, IPv6SendDataIdentifier, IOSendDataIdentifier {
	constructor(uBytes: Int) : this(uBytes.toLong() and 0xFFFFFFFF)
}