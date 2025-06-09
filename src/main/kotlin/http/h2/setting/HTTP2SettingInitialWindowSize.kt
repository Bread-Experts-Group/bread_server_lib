package org.bread_experts_group.http.h2.setting

import org.bread_experts_group.http.h2.HTTP2Frame
import org.bread_experts_group.stream.read32ul
import org.bread_experts_group.stream.write32
import java.io.InputStream
import java.io.OutputStream

class HTTP2SettingInitialWindowSize(
	val size: Long
) : HTTP2Setting(HTTP2SettingIdentifier.SETTINGS_INITIAL_WINDOW_SIZE) {
	override fun toString(): String = super.toString() + " [$size]"

	override fun write(stream: OutputStream) {
		stream.write32(size)
	}

	companion object {
		fun read(stream: InputStream): HTTP2SettingInitialWindowSize {
			val size = stream.read32ul()
			if (size > Int.MAX_VALUE)
				throw HTTP2Frame.HTTP2FlowControlError("Initial window size over ${Int.MAX_VALUE}, got $size")
			return HTTP2SettingInitialWindowSize(size)
		}
	}
}