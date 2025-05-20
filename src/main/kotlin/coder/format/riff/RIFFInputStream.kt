package org.bread_experts_group.coder.format.riff

import org.bread_experts_group.coder.format.Parser
import org.bread_experts_group.coder.format.riff.chunk.ContainerChunk
import org.bread_experts_group.coder.format.riff.chunk.RIFFChunk
import org.bread_experts_group.stream.read32
import org.bread_experts_group.stream.readString
import java.io.ByteArrayInputStream
import java.io.InputStream

class RIFFInputStream(from: InputStream) : Parser<String, RIFFChunk>(from) {
	private val chunkParsers = mutableMapOf<String, (InputStream) -> RIFFChunk>()
	override fun addParser(chunkIdentifier: String, parser: (InputStream) -> RIFFChunk) {
		if (chunkParsers.containsKey(chunkIdentifier))
			throw IllegalArgumentException("Parser for identifier \"$chunkIdentifier\" already exists")
		chunkParsers[chunkIdentifier] = parser
	}

	override fun readParsed(): RIFFChunk {
		val chunk = RIFFChunk(
			from.readString(4, Charsets.US_ASCII),
			from.read32().let {
				val data = from.readNBytes(it)
				if (it % 2L != 0L) from.read()
				data
			}
		)
		return chunkParsers[chunk.identifier]?.invoke(ByteArrayInputStream(chunk.data)) ?: chunk
	}


	fun containerChunk(identifier: String) {
		this.addParser(identifier) {
			ContainerChunk(
				identifier,
				RIFFInputStream(it).readAllParsed()
			)
		}
	}

	init {
		containerChunk("RIFF")
		containerChunk("LIST")
	}
}