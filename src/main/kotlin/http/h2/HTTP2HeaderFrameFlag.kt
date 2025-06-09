package org.bread_experts_group.http.h2

enum class HTTP2HeaderFrameFlag(val position: Int) {
	END_OF_STREAM(0x1),
	END_OF_HEADERS(0x4),
	PADDED(0x8),
	PRIORITY(0x20)
}