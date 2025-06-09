package org.bread_experts_group.http.h2.setting

import org.bread_experts_group.stream.read32ul
import org.bread_experts_group.stream.write32
import java.io.InputStream
import java.io.OutputStream

class HTTP2SettingMaxConcurrentStreams(
	val count: Long
) : HTTP2Setting(HTTP2SettingIdentifier.SETTINGS_MAX_CONCURRENT_STREAMS) {
	override fun toString(): String = super.toString() + " [$count]"

	override fun write(stream: OutputStream) {
		stream.write32(count)
	}

	companion object {
		fun read(stream: InputStream): HTTP2SettingMaxConcurrentStreams = HTTP2SettingMaxConcurrentStreams(
			stream.read32ul()
		)
	}
}