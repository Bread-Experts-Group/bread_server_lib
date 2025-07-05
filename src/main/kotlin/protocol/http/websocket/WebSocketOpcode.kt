package org.bread_experts_group.protocol.http.websocket

enum class WebSocketOpcode(val code: Int) {
	CONTINUATION(0),
	DATA_TEXT(1),
	DATA_BINARY(2),
	CLOSE(8),
	PING(9),
	PONG(10);

	companion object {
		val mapping: Map<Int, WebSocketOpcode> = entries.associateBy(WebSocketOpcode::code)
	}
}