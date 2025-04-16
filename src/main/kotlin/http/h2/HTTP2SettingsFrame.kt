package bread_experts_group.http.h2

import bread_experts_group.hex
import bread_experts_group.http.h2.setting.HTTP2Setting
import bread_experts_group.socket.read32
import java.io.InputStream

class HTTP2SettingsFrame(
	val flags: List<HTTP2SettingsFrameFlag>,
	val settings: List<HTTP2Setting>
) : HTTP2Frame(HTTP2FrameType.SETTINGS, 0) {
	override fun toString(): String = super.toString() + " [${flags.joinToString(" ")}], SETTINGS #: [${settings.size}]" +
			settings.joinToString("") { "\n$it" }

	companion object {
		fun read(stream: InputStream, length: Int): HTTP2SettingsFrame {
			if (length % 6 != 0)
				throw HTTP2FrameSizeError("SETTINGS frame size is not a multiple of 6, got ${length % 6} ($length)")
			val flagsRaw = stream.read()
			var flags = buildList { HTTP2SettingsFrameFlag.entries.forEach { if (it.position and flagsRaw > 0) add(it) } }
			if (flags.contains(HTTP2SettingsFrameFlag.ACKNOWLEDGED) && length != 0)
				throw HTTP2FrameSizeError("SETTINGS ACK frame size is not 0, got $length")
			val identifier = stream.read32()
			if (identifier != 0)
				throw HTTP2ProtocolError("SETTINGS frame ID was not 0x0, got ${hex(identifier)}")
			return HTTP2SettingsFrame(
				flags,
				buildList {
					var remaining = length
					while (remaining > 0) {
						add(HTTP2Setting.read(stream))
						remaining -= 6
					}
				}
			)
		}
	}
}