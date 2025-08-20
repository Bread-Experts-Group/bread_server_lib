package org.bread_experts_group.protocol.old.http.h2.setting

import org.bread_experts_group.stream.read32ul
import org.bread_experts_group.stream.write32
import java.io.InputStream
import java.io.OutputStream

class HTTP2SettingMaxHeaderListSize(
	val size: Long
) : HTTP2Setting(HTTP2SettingIdentifier.SETTINGS_MAX_HEADER_LIST_SIZE) {
	override fun toString(): String = super.toString() + " [$size]"

	override fun write(stream: OutputStream) {
		stream.write32(size)
	}

	companion object {
		fun read(stream: InputStream): HTTP2SettingMaxHeaderListSize = HTTP2SettingMaxHeaderListSize(
			stream.read32ul()
		)
	}
}