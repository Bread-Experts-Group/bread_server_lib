package org.bread_experts_group.coder.format.riff.chunk

import org.bread_experts_group.stream.write32
import org.bread_experts_group.stream.writeString
import java.io.OutputStream

data class RIFFContainerChunk(
	override val tag: String,
	val localIdentifier: String,
	val chunks: List<RIFFChunk>
) : RIFFChunk(tag, byteArrayOf()) {
	override fun toString(): String = "RIFFContainerChunk.\"$tag\"[\"$localIdentifier\":[${chunks.size}]$chunks]"
	override fun write(stream: OutputStream) {
		stream.writeString(tag)
		var data = byteArrayOf()
		chunks.forEach { data = data + it.asBytes() }
		stream.write32(Integer.reverseBytes(data.size + 4))
		stream.writeString(localIdentifier)
		stream.write(data)
	}
}