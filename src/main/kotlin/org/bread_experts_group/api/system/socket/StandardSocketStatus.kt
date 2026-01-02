package org.bread_experts_group.api.system.socket

import org.bread_experts_group.api.system.socket.close.SocketCloseDataIdentifier
import org.bread_experts_group.api.system.socket.ipv4.receive.IPv4ReceiveDataIdentifier
import org.bread_experts_group.api.system.socket.ipv4.send.IPv4SendDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.receive.IPv6ReceiveDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.send.IPv6SendDataIdentifier

enum class StandardSocketStatus :
	IPv4ReceiveDataIdentifier, IPv6ReceiveDataIdentifier,
	IPv4SendDataIdentifier, IPv6SendDataIdentifier,
	SocketCloseDataIdentifier {
	CONNECTION_CLOSED,
	NOT_CONNECTED,
	OPERATION_TIMEOUT
}