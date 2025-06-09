package org.bread_experts_group.coder.format.riff.chunk

import org.bread_experts_group.stream.Tagged
import org.bread_experts_group.stream.Writable
import org.bread_experts_group.stream.write32
import org.bread_experts_group.stream.writeString
import java.io.OutputStream

open class RIFFChunk(
	override val tag: String,
	val data: ByteArray
) : Writable, Tagged<String> {
	var parent: RIFFContainerChunk? = null
	override fun toString(): String = "RIFFChunk.\"$tag\"[${data.size}]"

	override fun computeSize(): Long = data.size.toLong()
	override fun write(stream: OutputStream) {
		stream.writeString(tag)
		stream.write32(Integer.reverseBytes(computeSize().toInt()))
		stream.write(data)
	}
}