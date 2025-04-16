package org.bread_experts_group.http.h2

import org.bread_experts_group.socket.read32
import java.io.InputStream

class HTTP2DataFrame(
	identifier: Int,
	val flags: List<HTTP2DataFrameFlag>,
	val data: ByteArray
) : HTTP2Frame(HTTP2FrameType.DATA, identifier) {
	override fun toString(): String = super.toString() + " [${flags.joinToString(" ")}], DATA #: [${data.size}]"

	companion object {
		fun read(stream: InputStream, length: Int): HTTP2DataFrame {
			val flagsRaw = stream.read()
			var flags = buildList { HTTP2DataFrameFlag.entries.forEach { if (it.position and flagsRaw > 0) add(it) } }
			val identifier = stream.read32()
			if (flags.contains(HTTP2DataFrameFlag.PADDED)) {
				var skip = stream.read()
				val frame = HTTP2DataFrame(identifier, flags, stream.readNBytes(length - 1 - skip))
				stream.skip(skip.toLong())
				return frame
			} else {
				return HTTP2DataFrame(identifier, flags, stream.readNBytes(length))
			}
		}
	}
}