package org.bread_experts_group.http.h2

import java.io.InputStream
import java.io.OutputStream

class HTTP2DataFrame(
	identifier: Int,
	val flags: List<HTTP2DataFrameFlag>,
	val data: ByteArray
) : HTTP2Frame(HTTP2FrameType.DATA, identifier) {
	override fun toString(): String = super.toString() + " [${flags.joinToString(" ")}], DATA #: [${data.size}]"

	override fun computeSize(): Long = data.size.toLong()
	override fun collectFlags(): Int = flags.fold(0) { flags, flag -> flags or flag.position }
	override fun write(stream: OutputStream) {
		super.write(stream)
		stream.write(data)
	}

	companion object {
		fun read(stream: InputStream, length: Int, flagsRaw: Int, identifier: Int): HTTP2DataFrame {
			val flags = buildList { HTTP2DataFrameFlag.entries.forEach { if (it.position and flagsRaw > 0) add(it) } }
			if (flags.contains(HTTP2DataFrameFlag.PADDED)) {
				val skip = stream.read()
				val frame = HTTP2DataFrame(identifier, flags, stream.readNBytes(length - 1 - skip))
				stream.skip(skip.toLong())
				return frame
			} else {
				return HTTP2DataFrame(identifier, flags, stream.readNBytes(length))
			}
		}
	}
}