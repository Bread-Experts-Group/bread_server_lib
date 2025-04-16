package bread_experts_group.http.h2.setting

import bread_experts_group.read32ul
import java.io.InputStream

class HTTP2SettingHeaderTableSize(
	val size: Long
) : HTTP2Setting(HTTP2SettingIdentifier.SETTINGS_HEADER_TABLE_SIZE) {
	override fun toString(): String = super.toString() + " [$size]"

	companion object {
		fun read(stream: InputStream): HTTP2SettingHeaderTableSize = HTTP2SettingHeaderTableSize(
			stream.read32ul()
		)
	}
}