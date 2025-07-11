package org.bread_experts_group.coder.format.parse.riff.chunk

import org.bread_experts_group.stream.writeString
import java.io.OutputStream

data class RIFFTextChunk(
	override val tag: String,
	val text: String
) : RIFFChunk(tag, byteArrayOf()) {
	override fun toString(): String = "RIFFChunk.\"${tag}\"[\"$text\"]"

	override fun computeSize(): Long = text.length.toLong()
	override fun write(stream: OutputStream) {
		super.write(stream)
		stream.writeString(text)
	}
}