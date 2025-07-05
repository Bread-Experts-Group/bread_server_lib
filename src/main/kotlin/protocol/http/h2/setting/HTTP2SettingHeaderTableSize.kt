package org.bread_experts_group.protocol.http.h2.setting

import org.bread_experts_group.stream.read32ul
import org.bread_experts_group.stream.write32
import java.io.InputStream
import java.io.OutputStream

class HTTP2SettingHeaderTableSize(
	val size: Long
) : HTTP2Setting(HTTP2SettingIdentifier.SETTINGS_HEADER_TABLE_SIZE) {
	override fun toString(): String = super.toString() + " [$size]"

	override fun write(stream: OutputStream) {
		stream.write32(size)
	}

	companion object {
		fun read(stream: InputStream): HTTP2SettingHeaderTableSize = HTTP2SettingHeaderTableSize(
			stream.read32ul()
		)
	}
}