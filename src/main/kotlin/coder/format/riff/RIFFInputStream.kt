package org.bread_experts_group.coder.format.riff

import org.bread_experts_group.coder.DecodingException
import org.bread_experts_group.coder.format.Parser
import org.bread_experts_group.coder.format.riff.chunk.AudioFormatChunk
import org.bread_experts_group.coder.format.riff.chunk.ContainerChunk
import org.bread_experts_group.coder.format.riff.chunk.RIFFChunk
import org.bread_experts_group.coder.format.riff.chunk.TextChunk
import org.bread_experts_group.stream.read16ui
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
			readString(4),
			Integer.reverseBytes(read32()).let {
				if (it < 0)
					throw DecodingException("Size is too big [${it.toUInt()}]!")
				val data = readNBytes(it)
				if (it % 2L != 0L) read()
				data
			}
		)
		return chunkParsers[chunk.identifier]?.invoke(ByteArrayInputStream(chunk.data)) ?: chunk
	}

	fun containerChunk(identifier: String) {
		this.addParser(identifier) {
			ContainerChunk(
				identifier,
				it.readString(4),
				RIFFInputStream(it).readAllParsed()
			)
		}
	}

	fun textChunk(identifier: String) {
		this.addParser(identifier) {
			TextChunk(
				identifier,
				it.readAllBytes().decodeToString()
			)
		}
	}

	init {
		containerChunk("RIFF")
		containerChunk("LIST")
		textChunk("IARL")
		textChunk("IART")
		textChunk("ICMS")
		textChunk("ICMT")
		textChunk("ICOP")
		textChunk("ICRD")
		textChunk("ICRP")
		textChunk("IDIM")
		textChunk("IDPI")
		textChunk("IENG")
		textChunk("IGNR")
		textChunk("IKEY")
		textChunk("ILGT")
		textChunk("IMED")
		textChunk("INAM")
		textChunk("IPLT")
		textChunk("IPRD")
		textChunk("ISBJ")
		textChunk("ISFT")
		textChunk("ISHP")
		textChunk("ISRC")
		textChunk("ISRF")
		textChunk("ITCH")
		this.addParser("fmt ") {
			AudioFormatChunk(
				AudioFormatChunk.AudioEncoding.mapping.getValue(Integer.reverseBytes(it.read16ui())),
				Integer.reverseBytes(it.read16ui()),
				Integer.reverseBytes(it.read32()),
				Integer.reverseBytes(it.read32()),
				Integer.reverseBytes(it.read32()),
				Integer.reverseBytes(it.read16ui()),
				it.readAllBytes()
			)
		}
	}
}