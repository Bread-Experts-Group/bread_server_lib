package bread_experts_group.http.h2.setting

import bread_experts_group.socket.read16ui
import java.io.InputStream

sealed class HTTP2Setting(val identifier: HTTP2SettingIdentifier) {
	override fun toString(): String = "(HTTP/2, Setting) ${identifier.name}"

	companion object {
		fun read(stream: InputStream): HTTP2Setting {
			val identifier = HTTP2SettingIdentifier.mapping[stream.read16ui()] ?: HTTP2SettingIdentifier.OTHER
			return when (identifier) {
				HTTP2SettingIdentifier.SETTINGS_HEADER_TABLE_SIZE -> HTTP2SettingHeaderTableSize.read(stream)
				HTTP2SettingIdentifier.SETTINGS_ENABLE_PUSH -> HTTP2SettingEnableServerPush.read(stream)
				HTTP2SettingIdentifier.SETTINGS_MAX_CONCURRENT_STREAMS -> HTTP2SettingMaxConcurrentStreams.read(stream)
				HTTP2SettingIdentifier.SETTINGS_MAX_FRAME_SIZE -> HTTP2SettingMaxFrameSize.read(stream)
				HTTP2SettingIdentifier.SETTINGS_INITIAL_WINDOW_SIZE -> HTTP2SettingInitialWindowSize.read(stream)
				HTTP2SettingIdentifier.SETTINGS_MAX_HEADER_LIST_SIZE -> HTTP2SettingMaxHeaderListSize.read(stream)
				HTTP2SettingIdentifier.OTHER -> HTTP2SettingUnknown.read(stream)
			}
		}
	}
}