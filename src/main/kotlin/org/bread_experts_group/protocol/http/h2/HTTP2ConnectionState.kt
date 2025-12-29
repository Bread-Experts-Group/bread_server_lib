package org.bread_experts_group.protocol.http.h2

data class HTTP2ConnectionState(
	var flowControl: Int,
	var closed: CloseState = CloseState.NOT_CLOSED
) {
	enum class CloseState {
		REMOTE,
		LOCAL,
		NOT_CLOSED
	}
}