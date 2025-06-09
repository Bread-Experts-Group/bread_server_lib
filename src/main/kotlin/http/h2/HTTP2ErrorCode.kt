package org.bread_experts_group.http.h2

enum class HTTP2ErrorCode(val code: Int) {
	OK(0x0),
	PROTOCOL_ERROR(0x1),
	INTERNAL_ERROR(0x2),
	FLOW_CONTROL_ERROR(0x3),
	SETTINGS_TIMEOUT(0x4),
	STREAM_CLOSED(0x5),
	FRAME_SIZE_ERROR(0x6),
	REFUSED_STREAM(0x7),
	STREAM_CANCEL(0x8),
	COMPRESSION_ERROR(0x9),
	CONNECT_ERROR(0xA),
	EXCESSIVE_LOAD(0xB),
	INADEQUATE_SECURITY(0xC),
	HTTP_1_1_REQUIRED(0xD);

	companion object {
		val mapping = entries.associateBy(HTTP2ErrorCode::code)
	}
}