package org.bread_experts_group.http.h2.setting

import org.bread_experts_group.http.h2.HTTP2Frame
import org.bread_experts_group.stream.read32ul
import org.bread_experts_group.stream.write32
import java.io.InputStream
import java.io.OutputStream

class HTTP2SettingEnableServerPush(
	val ok: Boolean
) : HTTP2Setting(HTTP2SettingIdentifier.SETTINGS_ENABLE_PUSH) {
	override fun toString(): String = super.toString() + " [$ok]"

	override fun write(stream: OutputStream) {
		stream.write32(if (ok) 1 else 0)
	}

	companion object {
		fun read(stream: InputStream): HTTP2SettingEnableServerPush {
			val state = stream.read32ul()
			if (state > 1)
				throw HTTP2Frame.HTTP2ProtocolError("Server push was not 0 or 1 (got $state)")
			return HTTP2SettingEnableServerPush(state == 1L)
		}
	}
}