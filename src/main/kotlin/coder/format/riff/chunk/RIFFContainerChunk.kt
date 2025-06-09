package org.bread_experts_group.coder.format.riff.chunk

import java.io.OutputStream

data class RIFFContainerChunk(
	override val tag: String,
	val localIdentifier: String,
	val chunks: List<RIFFChunk>
) : RIFFChunk(tag, byteArrayOf()) {
	override fun toString(): String = "RIFFContainerChunk.\"$tag\"[\"$localIdentifier\":[${chunks.size}]$chunks]"

	override fun computeSize(): Long = chunks.sumOf { it.computeSize() }
	override fun write(stream: OutputStream) {
		super.write(stream)
		chunks.forEach { it.write(stream) }
	}
}