package org.bread_experts_group.api.system.socket.listen

import org.bread_experts_group.api.system.socket.ipv4.receive.IPv4ReceiveFeatureIdentifier
import org.bread_experts_group.api.system.socket.ipv6.receive.IPv6ReceiveFeatureIdentifier

enum class WindowsReceiveFeatures : IPv4ReceiveFeatureIdentifier, IPv6ReceiveFeatureIdentifier {
	PEEK,
	OUT_OF_BAND,
	PARTIAL,
	HINT_NO_DELAY,
	WAIT_UNTIL_BUFFER_FULL
}