package org.bread_experts_group.coder.format.riff.chunk

import org.bread_experts_group.coder.format.riff.RIFFParser
import java.io.OutputStream

class RIFFContainerChunk(
	override val tag: String,
	val localIdentifier: String,
	private val chunks: RIFFParser
) : RIFFChunk(tag, byteArrayOf()), Iterable<RIFFChunk> {
	private val cachedChunks = mutableListOf<RIFFChunk>()
	private var chunksIndex = 0
	override fun iterator(): Iterator<RIFFChunk> = object : Iterator<RIFFChunk> {
		override fun hasNext(): Boolean = chunks.hasRemaining()
		override fun next(): RIFFChunk {
			val next = chunks.readParsed()
			next.parent = this@RIFFContainerChunk
			cachedChunks.add(next)
			return cachedChunks[chunksIndex++]
		}
	}

	override fun toString(): String = "RIFFContainerChunk.\"$tag\"[\"$localIdentifier\":[${cachedChunks.size}]$chunks]"
	override fun computeSize(): Long = cachedChunks.sumOf { it.computeSize() }
	override fun write(stream: OutputStream) {
		super.write(stream)
		cachedChunks.forEach { it.write(stream) }
	}
}