package org.bread_experts_group.protocol.old.http.h2.setting

import org.bread_experts_group.coder.Mappable

enum class HTTP2SettingIdentifier(
	override val id: UShort,
	override val tag: String
) : Mappable<HTTP2SettingIdentifier, UShort> {
	SETTINGS_HEADER_TABLE_SIZE(1u, "Header Table Size"),
	SETTINGS_ENABLE_PUSH(2u, "Server Push"),
	SETTINGS_MAX_CONCURRENT_STREAMS(3u, "Maximum Concurrent Streams"),
	SETTINGS_INITIAL_WINDOW_SIZE(4u, "Initial Window Size"),
	SETTINGS_MAX_FRAME_SIZE(5u, "Maximum Frame Size"),
	SETTINGS_MAX_HEADER_LIST_SIZE(6u, "Maximum Header List Size"), ;

	override fun toString(): String = stringForm()
}