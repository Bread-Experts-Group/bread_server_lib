package org.bread_experts_group.coder.format.iso_bmff

import org.bread_experts_group.coder.DecodingException
import org.bread_experts_group.coder.format.Parser
import org.bread_experts_group.coder.format.iso_bmff.box.*
import org.bread_experts_group.stream.*
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class ISOBMFFInputStream(from: InputStream) : Parser<String, ISOBMFFBox>("ISO Base Media File Format", from) {
	override fun readParsed(): ISOBMFFBox {
		var size = this.read32ul() - 8
		val name = this.readString(4)
		if (size == 1L) size = this.read64()
		if (size > Int.MAX_VALUE) throw DecodingException("Size is too big [${size.toULong()}]!")
		val data = if (size == 0L) this.readAllBytes()
		else this.readNBytes(size.toInt())
		val element = ISOBMFFBox(name, data)
		val parser = this.parsers[element.name]
		this.logger.fine {
			"Read generic box \"${element.name}\", size [${element.data.size}]" +
					if (parser != null) " | responsible parser: $parser"
					else ""
		}
		return parser?.invoke(ByteArrayInputStream(element.data))?.also {
			this.logger.fine { "Parsed box into [${it.javaClass.canonicalName}] from [$parser], $element" }
		} ?: element
	}

	fun containerBox(name: String) {
		this.addParser(name) {
			ISOBMFFContainerBox(
				name,
				ISOBMFFInputStream(it).readAllParsed()
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
		addParser("cprt") {
			ISOBMFFCopyrightBox(
				it.read16ui(),
				it.readString(it.available())
			)
		}
		addParser("ftyp") {
			ISOBMFFFileTypeCompatibilityBox(
				it.readString(4),
				it.read32(),
				buildList {
					while (it.available() > 0) add(it.readString(4))
				}
			)
		}
		addParser("mvhd") {
			when (val version = it.read()) {
				0 -> ISOMBFFMovieHeaderBoxV0(
					it.read24(),
					ZonedDateTime.ofInstant(
						Instant.ofEpochSecond(it.read32ul()),
						ZoneId.of("UTC")
					).minusYears(66),
					ZonedDateTime.ofInstant(
						Instant.ofEpochSecond(it.read32ul()),
						ZoneId.of("UTC")
					).minusYears(66),
					it.read32(),
					it.read32(),
					it.read16ui() + (it.read16ui() / 65536.0),
					it.read() + (it.read() / 256.0),
					it.readNBytes(10),
					(Array(9) { _ -> it.read32() }).toIntArray(),
					it.readNBytes(24),
					it.read32()
				)

				1 -> ISOMBFFMovieHeaderBoxV1(
					it.read24(),
					ZonedDateTime.ofInstant(
						Instant.ofEpochSecond(it.read64()),
						ZoneId.of("UTC")
					).plusYears(70),
					ZonedDateTime.ofInstant(
						Instant.ofEpochSecond(it.read64()),
						ZoneId.of("UTC")
					).plusYears(70),
					it.read32(),
					it.read64(),
					it.read16ui() + (it.read16ui() / 65536.0),
					it.read() + (it.read() / 256.0),
					it.readNBytes(10),
					(Array(9) { _ -> it.read32() }).toIntArray(),
					it.readNBytes(24),
					it.read32()
				)

				else -> throw DecodingException("Unsupported version: $version")
			}
		}
	}
}