package bread_experts_group.http.h2.setting

import bread_experts_group.http.h2.HTTP2Frame
import bread_experts_group.read32ul
import java.io.InputStream

class HTTP2SettingInitialWindowSize(
	val size: Long
) : HTTP2Setting(HTTP2SettingIdentifier.SETTINGS_INITIAL_WINDOW_SIZE) {
	override fun toString(): String = super.toString() + " [$size]"

	companion object {
		fun read(stream: InputStream): HTTP2SettingInitialWindowSize {
			val size = stream.read32ul()
			if (size > Int.MAX_VALUE)
				throw HTTP2Frame.HTTP2FlowControlError("Initial window size over ${Int.MAX_VALUE}, got $size")
			return HTTP2SettingInitialWindowSize(size)
		}
	}
}