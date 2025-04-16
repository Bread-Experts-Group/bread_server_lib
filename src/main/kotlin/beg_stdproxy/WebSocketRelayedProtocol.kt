package org.bread_experts_group.beg_stdproxy

enum class WebSocketRelayedProtocol(val code: Int) {
	TRANSMISSION_CONTROL_PROTOCOL(0x00),
	USER_DATAGRAM_PROTOCOL(0x01);

	companion object {
		val mapping = entries.associateBy(WebSocketRelayedProtocol::code)
	}
}