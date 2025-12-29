package org.bread_experts_group.protocol.http.h2

import org.bread_experts_group.Mappable

enum class HTTP2StandardSettings(override val id: Int) : Mappable<HTTP2StandardSettings, Int> {
	SETTINGS_HEADER_TABLE_SIZE(0x01),
	SETTINGS_ENABLE_PUSH(0x02),
	SETTINGS_MAX_CONCURRENT_STREAMS(0x03),
	SETTINGS_INITIAL_WINDOW_SIZE(0x04),
	SETTINGS_MAX_FRAME_SIZE(0x05);

	override val tag: String = name
	override fun toString(): String = stringForm()
}