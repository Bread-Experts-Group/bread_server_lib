package bread_experts_group.http.h2.setting

import bread_experts_group.socket.read32ul
import java.io.InputStream

class HTTP2SettingMaxConcurrentStreams(
	val count: Long
) : HTTP2Setting(HTTP2SettingIdentifier.SETTINGS_MAX_CONCURRENT_STREAMS) {
	override fun toString(): String = super.toString() + " [$count]"

	companion object {
		fun read(stream: InputStream): HTTP2SettingMaxConcurrentStreams = HTTP2SettingMaxConcurrentStreams(
			stream.read32ul()
		)
	}
}