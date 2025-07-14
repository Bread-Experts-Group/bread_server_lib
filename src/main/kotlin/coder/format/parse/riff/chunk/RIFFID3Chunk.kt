package org.bread_experts_group.coder.format.parse.riff.chunk

import org.bread_experts_group.coder.CodingException
import org.bread_experts_group.coder.LazyPartialResult
import org.bread_experts_group.coder.format.parse.Parser
import org.bread_experts_group.coder.format.parse.id3.frame.ID3Frame
import java.io.InputStream

class RIFFID3Chunk(
	val id3: Parser<String, ID3Frame<*>, InputStream>
) : RIFFChunk("id3 ", byteArrayOf()), Iterable<LazyPartialResult<ID3Frame<*>, CodingException>> {
	override fun iterator(): Iterator<LazyPartialResult<ID3Frame<*>, CodingException>> = id3.iterator()
	override fun toString(): String = "RIFFID3Chunk[$id3]"
}