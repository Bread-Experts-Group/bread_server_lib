package org.bread_experts_group.coder.format.riff

import org.bread_experts_group.coder.DecodingException
import org.bread_experts_group.coder.format.Parser
import org.bread_experts_group.coder.format.riff.chunk.ContainerChunk
import org.bread_experts_group.coder.format.riff.chunk.RIFFChunk
import org.bread_experts_group.stream.read32
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
			readString(4, Charsets.US_ASCII),
			read32().let {
				val flipBuffer = ByteBuffer.allocateDirect(4)
				flipBuffer.order(ByteOrder.LITTLE_ENDIAN)
				flipBuffer.putInt(it)
				flipBuffer.order(ByteOrder.BIG_ENDIAN)
				flipBuffer.flip()
				val realSize = flipBuffer.getInt()
				if (realSize < 0)
					throw DecodingException("Size is too big [${realSize.toUInt()}]!")
				val data = readNBytes(realSize)
				if (realSize % 2L != 0L) read()
				data
			}
		)
		return chunkParsers[chunk.identifier]?.invoke(ByteArrayInputStream(chunk.data)) ?: chunk
	}

	fun containerChunk(identifier: String) {
		this.addParser(identifier) {
			ContainerChunk(
				identifier,
				it.readString(4, Charsets.US_ASCII),
				RIFFInputStream(it).readAllParsed()
			)
		}
	}

	init {
		containerChunk("RIFF")
		containerChunk("LIST")
	}
}