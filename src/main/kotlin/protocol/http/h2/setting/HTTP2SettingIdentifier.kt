package org.bread_experts_group.protocol.http.h2.setting

enum class HTTP2SettingIdentifier(val code: Int) {
	SETTINGS_HEADER_TABLE_SIZE(1),
	SETTINGS_ENABLE_PUSH(2),
	SETTINGS_MAX_CONCURRENT_STREAMS(3),
	SETTINGS_INITIAL_WINDOW_SIZE(4),
	SETTINGS_MAX_FRAME_SIZE(5),
	SETTINGS_MAX_HEADER_LIST_SIZE(6),
	OTHER(-1);

	companion object {
		val mapping: Map<Int, HTTP2SettingIdentifier> = entries.associateBy(HTTP2SettingIdentifier::code)
	}
}