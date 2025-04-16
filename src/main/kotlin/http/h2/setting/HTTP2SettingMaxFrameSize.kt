package org.bread_experts_group.http.h2.setting

import org.bread_experts_group.http.h2.HTTP2Frame
import org.bread_experts_group.socket.read32ul
import java.io.InputStream

class HTTP2SettingMaxFrameSize(
	val size: Long
) : HTTP2Setting(HTTP2SettingIdentifier.SETTINGS_MAX_FRAME_SIZE) {
	override fun toString(): String = super.toString() + " [$size]"

	companion object {
		fun read(stream: InputStream): HTTP2SettingMaxFrameSize {
			val size = stream.read32ul()
			if (size !in 16384..16777215)
				throw HTTP2Frame.HTTP2ProtocolError("Max frame size out of range 16384..16777215, got $size")
			return HTTP2SettingMaxFrameSize(size)
		}
	}
}