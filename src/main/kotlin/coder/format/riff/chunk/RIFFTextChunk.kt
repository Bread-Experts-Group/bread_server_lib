package org.bread_experts_group.coder.format.riff.chunk

import org.bread_experts_group.stream.write32
import org.bread_experts_group.stream.writeString
import java.io.OutputStream

data class RIFFTextChunk(
	override val tag: String,
	val text: String
) : RIFFChunk(tag, byteArrayOf()) {
	override fun toString(): String = "RIFFChunk.\"${tag}\"[\"$text\"]"
	override fun write(stream: OutputStream) {
		stream.writeString(tag)
		stream.write32(Integer.reverseBytes(text.length))
		stream.writeString(text)
	}
}