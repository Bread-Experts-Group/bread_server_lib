package org.bread_experts_group.coder.format.parse.riff.chunk

import org.bread_experts_group.coder.CodingException
import org.bread_experts_group.coder.LazyPartialResult
import org.bread_experts_group.coder.format.parse.riff.RIFFParser
import java.io.OutputStream

class RIFFContainerChunk(
	override val tag: String,
	val localIdentifier: String,
	private val chunks: RIFFParser
) : RIFFChunk(tag, byteArrayOf()), Iterable<LazyPartialResult<RIFFChunk, CodingException>> {
	override fun iterator(): Iterator<LazyPartialResult<RIFFChunk, CodingException>> = chunks.iterator()
	override fun toString(): String = "RIFFContainerChunk.\"$tag\"[\"$localIdentifier\"]"
	override fun computeSize(): Long = chunks.sumOf { it.resultSafe.computeSize() }
	override fun write(stream: OutputStream) {
		super.write(stream)
		chunks.forEach { it.resultSafe.write(stream) }
	}
}