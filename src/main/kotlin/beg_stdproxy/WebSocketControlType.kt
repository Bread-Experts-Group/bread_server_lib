package org.bread_experts_group.beg_stdproxy

enum class WebSocketControlType(val code: Int) {
	CONNECT(0x00),
	MESSAGE(0x01),
	DISCONNECT(0x02);

	companion object {
		val mapping = entries.associateBy(WebSocketControlType::code)
	}
}