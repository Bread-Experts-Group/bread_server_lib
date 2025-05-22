package org.bread_experts_group.coder.format.riff

import org.bread_experts_group.coder.DecodingException
import org.bread_experts_group.coder.format.Parser
import org.bread_experts_group.coder.format.riff.chunk.AudioFormatChunk
import org.bread_experts_group.coder.format.riff.chunk.ContainerChunk
import org.bread_experts_group.coder.format.riff.chunk.RIFFChunk
import org.bread_experts_group.coder.format.riff.chunk.TextChunk
import org.bread_experts_group.stream.read16
import org.bread_experts_group.stream.read32
import org.bread_experts_group.stream.readString
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.lang.Short
import kotlin.String
import kotlin.also
import kotlin.let
import kotlin.text.decodeToString
import kotlin.toUInt

class RIFFInputStream(from: InputStream) : Parser<String, RIFFChunk>("Resource Interchange File Format", from) {
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
		val parser = this.parsers[chunk.identifier]
		this.logger.fine {
			"Read generic chunk [${chunk.identifier}], size [${chunk.data.size}]" +
					if (parser != null) " | responsible parser: $parser"
					else ""
		}
		return parser?.invoke(ByteArrayInputStream(chunk.data))?.also {
			this.logger.fine { "Parsed chunk into [${it.javaClass.canonicalName}] from [$parser], $chunk" }
		} ?: chunk
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
				AudioFormatChunk.AudioEncoding.mapping.getValue(Short.reverseBytes(it.read16()).toInt()),
				Short.reverseBytes(it.read16()).toInt(),
				Integer.reverseBytes(it.read32()),
				Integer.reverseBytes(it.read32()),
				Short.reverseBytes(it.read16()).toInt(),
				Short.reverseBytes(it.read16()).toInt(),
				it.readAllBytes()
			)
		}
	}
}