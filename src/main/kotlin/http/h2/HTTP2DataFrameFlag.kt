package org.bread_experts_group.http.h2

enum class HTTP2DataFrameFlag(val position: Int) {
	END_OF_STREAM(0x1),
	PADDED(0x8)
}