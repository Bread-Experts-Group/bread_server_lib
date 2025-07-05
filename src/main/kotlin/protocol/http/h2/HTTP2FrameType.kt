package org.bread_experts_group.protocol.http.h2

enum class HTTP2FrameType(val code: Int) {
	DATA(0),
	HEADERS(1),
	PRIORITY(2),
	STOP_STREAM(3),
	SETTINGS(4),
	PUSH_PROMISE(5),
	PING(6),
	SHUTDOWN(7),
	WINDOW_UPDATE(8),
	CONTINUATION(9);

	companion object {
		val mapping: Map<Int, HTTP2FrameType> = entries.associateBy(HTTP2FrameType::code)
	}
}