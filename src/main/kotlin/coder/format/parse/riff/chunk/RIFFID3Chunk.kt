package org.bread_experts_group.coder.format.parse.riff.chunk

import org.bread_experts_group.coder.CodingException
import org.bread_experts_group.coder.LazyPartialResult
import org.bread_experts_group.coder.format.parse.id3.ID3Parser
import org.bread_experts_group.coder.format.parse.id3.frame.ID3Frame

class RIFFID3Chunk(
	val id3: ID3Parser
) : RIFFChunk("id3 ", byteArrayOf()), Iterable<LazyPartialResult<ID3Frame<*>, CodingException>> {
	override fun iterator(): Iterator<LazyPartialResult<ID3Frame<*>, CodingException>> = id3.iterator()
	override fun toString(): String = "RIFFID3Chunk[$id3]"
}