package org.bread_experts_group.coder.format.riff.chunk

import org.bread_experts_group.coder.format.id3.ID3Parser
import org.bread_experts_group.coder.format.id3.frame.ID3Frame

class RIFFID3Chunk(
	val id3: ID3Parser
) : RIFFChunk("id3 ", byteArrayOf()), Iterable<ID3Frame<*>> {
	override fun iterator(): Iterator<ID3Frame<*>> = id3.iterator()
	override fun toString(): String = "RIFFID3Chunk[$id3]"
}