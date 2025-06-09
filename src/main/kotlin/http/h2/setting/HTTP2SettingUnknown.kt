package org.bread_experts_group.http.h2.setting

import org.bread_experts_group.stream.write32
import java.io.InputStream
import java.io.OutputStream

class HTTP2SettingUnknown : HTTP2Setting(HTTP2SettingIdentifier.OTHER) {
	override fun write(stream: OutputStream) {
		stream.write32(0)
	}

	companion object {
		fun read(stream: InputStream): HTTP2SettingUnknown {
			stream.skip(4)
			return HTTP2SettingUnknown()
		}
	}
}