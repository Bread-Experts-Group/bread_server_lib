package org.bread_experts_group.coder.format.riff

import org.bread_experts_group.coder.DecodingException
import org.bread_experts_group.coder.format.Parser
import org.bread_experts_group.coder.format.riff.chunk.ContainerChunk
import org.bread_experts_group.coder.format.riff.chunk.RIFFChunk
import org.bread_experts_group.stream.read32ul
import org.bread_experts_group.stream.readString
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

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
			from.read32ul().let {
				if (it > Int.MAX_VALUE)
					throw DecodingException("Size is over Int.MAX_VALUE! [$it] I cannot parse this yet!")
				val flipBuffer = ByteBuffer.allocateDirect(8)
				flipBuffer.order(ByteOrder.LITTLE_ENDIAN)
				flipBuffer.putLong(it)
				flipBuffer.order(ByteOrder.BIG_ENDIAN)
				flipBuffer.flip()
				val data = from.readNBytes(flipBuffer.getInt())
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