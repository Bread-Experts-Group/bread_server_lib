package org.bread_experts_group.protocol.old.http.h2

import org.bread_experts_group.hex
import org.bread_experts_group.protocol.old.http.h2.setting.HTTP2Setting
import org.bread_experts_group.stream.write16
import java.io.InputStream
import java.io.OutputStream

class HTTP2SettingsFrame(
	val flags: List<HTTP2SettingsFrameFlag>,
	val settings: List<HTTP2Setting>
) : HTTP2Frame(HTTP2FrameType.SETTINGS, 0) {
	override fun toString(): String = super.toString() + " [${flags.joinToString(" ")}], " +
			"SETTINGS #: [${settings.size}]" + settings.joinToString("") { "\n$it" }

	override fun computeSize(): Long = settings.size * 6L
	override fun collectFlags(): Int = flags.fold(0) { flags, flag -> flags or flag.position }
	override fun write(stream: OutputStream) {
		super.write(stream)
		settings.forEach {
			stream.write16(it.identifier.id.toInt())
			it.write(stream)
		}
	}

	companion object {
		fun read(stream: InputStream, length: Int, flagsRaw: Int, identifier: Int): HTTP2SettingsFrame {
			if (length % 6 != 0)
				throw HTTP2FrameSizeError("SETTINGS frame size is not a multiple of 6, got ${length % 6} ($length)")
			val flags = buildList {
				HTTP2SettingsFrameFlag.entries.forEach { if (it.position and flagsRaw > 0) add(it) }
			}
			if (flags.contains(HTTP2SettingsFrameFlag.ACKNOWLEDGED) && length != 0)
				throw HTTP2FrameSizeError("SETTINGS ACK frame size is not 0, got $length")
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