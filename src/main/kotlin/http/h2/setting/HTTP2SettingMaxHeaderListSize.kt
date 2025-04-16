package bread_experts_group.http.h2.setting

import bread_experts_group.read32ul
import java.io.InputStream

class HTTP2SettingMaxHeaderListSize(
	val size: Long
) : HTTP2Setting(HTTP2SettingIdentifier.SETTINGS_MAX_HEADER_LIST_SIZE) {
	override fun toString(): String = super.toString() + " [$size]"

	companion object {
		fun read(stream: InputStream): HTTP2SettingMaxHeaderListSize = HTTP2SettingMaxHeaderListSize(
			stream.read32ul()
		)
	}
}