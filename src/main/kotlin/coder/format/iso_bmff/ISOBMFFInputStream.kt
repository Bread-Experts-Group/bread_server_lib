package org.bread_experts_group.coder.format.iso_bmff

import org.bread_experts_group.coder.DecodingException
import org.bread_experts_group.coder.format.Parser
import org.bread_experts_group.coder.format.iso_bmff.box.*
import org.bread_experts_group.stream.*
import java.io.InputStream
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class ISOBMFFInputStream(
	from: InputStream
) : Parser<String, ISOBMFFBox, InputStream>("ISO Base Media File Format", from) {
	override fun responsibleStream(of: ISOBMFFBox): InputStream = of.data.inputStream()

	override fun readBase(): ISOBMFFBox {
		var size = this.read32ul() - 8
		val name = this.readString(4)
		if (size == 1L) size = this.read64()
		if (size > Int.MAX_VALUE) throw DecodingException("Size is too big [${size.toULong()}]!")
		val data = if (size == 0L) this.readAllBytes()
		else this.readNBytes(size.toInt())
		return ISOBMFFBox(name, data)
	}

	fun containerBox(name: String) {
		this.addParser(name) { stream, chunk ->
			ISOBMFFContainerBox(
				chunk.tag,
				ISOBMFFInputStream(stream).readAllParsed()
			)
		}
	}

	init {
		containerBox("moov")
		containerBox("trak")
		containerBox("mdia")
		containerBox("udta")
		containerBox("minf")
		containerBox("dinf")
		containerBox("stbl")
		addParser("cprt") { stream, chunk ->
			ISOBMFFCopyrightBox(
				stream.read16ui(),
				stream.readAllBytes().decodeToString()
			)
		}
		addParser("ftyp") { stream, chunk ->
			ISOBMFFFileTypeCompatibilityBox(
				stream.readString(4),
				stream.read32(),
				buildList {
					while (stream.available() > 0) add(stream.readString(4))
				}
			)
		}
		addParser("mvhd") { stream, chunk ->
			when (val version = stream.read()) {
				0 -> ISOMBFFMovieHeaderBoxV0(
					stream.read24(),
					ZonedDateTime.ofInstant(
						Instant.ofEpochSecond(stream.read32ul()),
						ZoneId.of("UTC")
					).minusYears(66),
					ZonedDateTime.ofInstant(
						Instant.ofEpochSecond(stream.read32ul()),
						ZoneId.of("UTC")
					).minusYears(66),
					stream.read32(),
					stream.read32(),
					stream.read16ui() + (stream.read16ui() / 65536.0),
					stream.read() + (stream.read() / 256.0),
					stream.readNBytes(10),
					(Array(9) { _ -> stream.read32() }).toIntArray(),
					stream.readNBytes(24),
					stream.read32()
				)

				1 -> ISOMBFFMovieHeaderBoxV1(
					stream.read24(),
					ZonedDateTime.ofInstant(
						Instant.ofEpochSecond(stream.read64()),
						ZoneId.of("UTC")
					).plusYears(70),
					ZonedDateTime.ofInstant(
						Instant.ofEpochSecond(stream.read64()),
						ZoneId.of("UTC")
					).plusYears(70),
					stream.read32(),
					stream.read64(),
					stream.read16ui() + (stream.read16ui() / 65536.0),
					stream.read() + (stream.read() / 256.0),
					stream.readNBytes(10),
					(Array(9) { _ -> stream.read32() }).toIntArray(),
					stream.readNBytes(24),
					stream.read32()
				)

				else -> throw DecodingException("Unsupported version: $version")
			}
		}
	}
}