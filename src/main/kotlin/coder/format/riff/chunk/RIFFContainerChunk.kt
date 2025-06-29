package org.bread_experts_group.coder.format.riff.chunk

import org.bread_experts_group.coder.format.riff.RIFFParser
import java.io.OutputStream

class RIFFContainerChunk(
	override val tag: String,
	val localIdentifier: String,
	private val chunks: RIFFParser
) : RIFFChunk(tag, byteArrayOf()), Iterable<RIFFChunk> {
	override fun iterator(): Iterator<RIFFChunk> = chunks.iterator()
	override fun toString(): String = "RIFFContainerChunk.\"$tag\"[\"$localIdentifier\"]"
	override fun computeSize(): Long = chunks.sumOf { it.computeSize() }
	override fun write(stream: OutputStream) {
		super.write(stream)
		chunks.forEach { it.write(stream) }
	}
}