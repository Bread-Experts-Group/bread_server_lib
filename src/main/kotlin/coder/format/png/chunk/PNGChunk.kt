package org.bread_experts_group.taggart.png.chunk

import org.bread_experts_group.stream.Tagged
import org.bread_experts_group.stream.Writable
import org.bread_experts_group.stream.write32
import org.bread_experts_group.stream.writeString
import java.io.OutputStream

open class PNGChunk(
	override val tag: String,
	open val data: ByteArray
) : Tagged<String>, Writable {
	override fun write(stream: OutputStream) {
		stream.write32(data.size)
		stream.writeString(tag)
		stream.write(data)
		stream.write32(0) // TODO CRC
	}

	override fun computeSize(): Long = data.size.toLong()
	override fun toString(): String = "PNGChunk.\"$tag\"[${computeSize()}]"
}