package org.bread_experts_group.coder.format.riff

import org.bread_experts_group.coder.DecodingException
import org.bread_experts_group.coder.format.Parser
import org.bread_experts_group.coder.format.riff.chunk.RIFFAudioFormatChunk
import org.bread_experts_group.coder.format.riff.chunk.RIFFChunk
import org.bread_experts_group.coder.format.riff.chunk.RIFFContainerChunk
import org.bread_experts_group.coder.format.riff.chunk.RIFFTextChunk
import org.bread_experts_group.stream.le
import org.bread_experts_group.stream.read16
import org.bread_experts_group.stream.read32
import org.bread_experts_group.stream.readString
import java.io.InputStream

open class RIFFParser(
	from: InputStream
) : Parser<String, RIFFChunk, InputStream>("Resource Interchange File Format", from) {
	override fun responsibleStream(of: RIFFChunk): InputStream = of.data.inputStream()

	override fun readBase(): RIFFChunk {
		val tag = fqIn.readString(4)
		return RIFFChunk(
			tag,
			fqIn.read32().le().let {
				if (it < 0)
					throw DecodingException("[$tag] Size is too big [${it.toUInt()}]!")
				val data = fqIn.readNBytes(it)
				if (it % 2L != 0L) fqIn.read()
				data
			}
		)
	}

	override var next: RIFFChunk? = refineNext()

	fun containerChunk(identifier: String) {
		this.addParser(identifier) { stream, chunk ->
			val containerChunk = RIFFContainerChunk(
				chunk.tag,
				stream.readString(4),
				RIFFParser(stream)
			)
			containerChunk
		}
	}

	fun textChunk(identifier: String) {
		this.addParser(identifier) { stream, chunk ->
			RIFFTextChunk(
				identifier,
				stream.readAllBytes().decodeToString()
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
		this.addParser("fmt ") { stream, chunk ->
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
	}
}