package org.bread_experts_group.http.h2

import org.bread_experts_group.hex
import org.bread_experts_group.stream.read32
import org.bread_experts_group.stream.read32ul
import java.io.InputStream

class HTTP2HeaderFrame(
	identifier: Int,
	val flags: List<HTTP2HeaderFrameFlag>,
	val priority: Priority?,
	val block: ByteArray
) : HTTP2Frame(HTTP2FrameType.HEADERS, identifier) {
	override fun toString(): String = super.toString() + " [${flags.joinToString(" ")}], DATA #: [${block.size}]" +
			if (priority != null) "\n$priority" else ""

	data class Priority(
		val exclusive: Boolean,
		val dependency: Int,
		val weight: Int
	) {
		override fun toString(): String = "(HTTP/2, HEADERS Priority) [${if (exclusive) "EX" else ""}] " +
				"DEP: ${hex(dependency)}, WHT: $weight"
	}

	companion object {
		fun read(stream: InputStream, length: Int): HTTP2HeaderFrame {
			val flagsRaw = stream.read()
			val flags = buildList { HTTP2HeaderFrameFlag.entries.forEach { if (it.position and flagsRaw > 0) add(it) } }
			val identifier = stream.read32()
			if (identifier == 0)
				throw HTTP2ProtocolError("Header frame identifier must be non-zero, got ${hex(identifier)}")
			var remainder = length
			val padding = if (flags.contains(HTTP2HeaderFrameFlag.PADDED)) {
				val padding = stream.read()
				remainder -= padding + 1
				padding
			} else 0
			val priority = if (flags.contains(HTTP2HeaderFrameFlag.PRIORITY)) {
				val depE = stream.read32ul()
				remainder -= 5
				Priority(
					depE > Int.MAX_VALUE,
					(depE and 0x7FFFFFFF).toInt(),
					stream.read()
				)
			} else null
			val block = stream.readNBytes(remainder)
			stream.skip(padding.toLong())
			return HTTP2HeaderFrame(identifier, flags, priority, block)
		}
	}
}