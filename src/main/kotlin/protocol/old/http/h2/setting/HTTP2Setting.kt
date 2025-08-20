package org.bread_experts_group.protocol.old.http.h2.setting

import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.stream.Writable
import org.bread_experts_group.stream.read16u
import java.io.InputStream

sealed class HTTP2Setting(val identifier: HTTP2SettingIdentifier) : Writable {
	override fun toString(): String = "(HTTP/2, Setting) ${identifier.name}"

	companion object {
		fun read(stream: InputStream): HTTP2Setting {
			val identifier = HTTP2SettingIdentifier.entries.id(stream.read16u()).enum!!
			return when (identifier) {
				HTTP2SettingIdentifier.SETTINGS_HEADER_TABLE_SIZE -> HTTP2SettingHeaderTableSize.read(stream)
				HTTP2SettingIdentifier.SETTINGS_ENABLE_PUSH -> HTTP2SettingEnableServerPush.read(stream)
				HTTP2SettingIdentifier.SETTINGS_MAX_CONCURRENT_STREAMS -> HTTP2SettingMaxConcurrentStreams.read(stream)
				HTTP2SettingIdentifier.SETTINGS_MAX_FRAME_SIZE -> HTTP2SettingMaxFrameSize.read(stream)
				HTTP2SettingIdentifier.SETTINGS_INITIAL_WINDOW_SIZE -> HTTP2SettingInitialWindowSize.read(stream)
				HTTP2SettingIdentifier.SETTINGS_MAX_HEADER_LIST_SIZE -> HTTP2SettingMaxHeaderListSize.read(stream)
			}
		}
	}
}