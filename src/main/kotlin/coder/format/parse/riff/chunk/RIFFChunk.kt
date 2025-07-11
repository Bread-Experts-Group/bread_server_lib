package org.bread_experts_group.coder.format.parse.riff.chunk

import org.bread_experts_group.stream.*
import java.io.OutputStream

open class RIFFChunk(
	override val tag: String,
	val data: ByteArray
) : Writable, Tagged<String> {
	var parent: RIFFContainerChunk? = null
	override fun toString(): String = "RIFFChunk.\"$tag\"[#${data.size}]"

	override fun computeSize(): Long = data.size.toLong()
	override fun write(stream: OutputStream) {
		stream.writeString(tag)
		stream.write32(computeSize().toInt().le())
		stream.write(data)
	}
}