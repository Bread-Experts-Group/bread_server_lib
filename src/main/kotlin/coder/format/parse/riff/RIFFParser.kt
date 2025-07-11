package org.bread_experts_group.coder.format.parse.riff

import org.bread_experts_group.coder.format.parse.CodingCompoundThrowable
import org.bread_experts_group.coder.format.parse.Parser
import org.bread_experts_group.coder.format.parse.id3.ID3Parser
import org.bread_experts_group.coder.format.parse.riff.chunk.*
import org.bread_experts_group.stream.le
import org.bread_experts_group.stream.read16
import org.bread_experts_group.stream.read32
import org.bread_experts_group.stream.readString
import java.io.InputStream

open class RIFFParser(
	from: InputStream
) : Parser<String, RIFFChunk, InputStream>("Resource Interchange File Format", from) {
	override fun responsibleStream(of: RIFFChunk): InputStream = of.data.inputStream()

	override fun readBase(compound: CodingCompoundThrowable): RIFFChunk {
		val tag = fqIn.readString(4)
		return RIFFChunk(
			tag,
			fqIn.read32().le().let {
				if (it < 0)
					throw UnsupportedOperationException("[$tag] Size is too big [${it.toUInt()}]!")
				val data = fqIn.readNBytes(it)
				if (it % 2L != 0L) fqIn.read()
				data
			}
		)
	}

	fun containerChunk(identifier: String) {
		this.addParser(identifier) { stream, chunk, _ ->
			val containerChunk = RIFFContainerChunk(
				chunk.tag,
				stream.readString(4),
				RIFFParser(stream)
			)
			containerChunk
		}
	}

	fun textChunk(identifier: String) {
		this.addParser(identifier) { stream, chunk, _ ->
			RIFFTextChunk(
				identifier,
				stream.readString(Charsets.ISO_8859_1)
			)
		}
	}

	init {
		containerChunk("RIFF")
		containerChunk("LIST")
		textChunk("ITRK")
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
		addParser("fmt ") { stream, chunk, _ ->
			RIFFAudioFormatChunk(
				RIFFAudioFormatChunk.AudioEncoding.mapping.getValue(stream.read16().le().toInt()),
				stream.read16().le().toInt(),
				stream.read32().le(),
				stream.read32().le(),
				stream.read16().le().toInt(),
				stream.read16().le().toInt(),
				stream.readAllBytes()
			)
		}
		addParser("id3 ") { stream, chunk, _ -> RIFFID3Chunk(ID3Parser(stream)) }
	}
}