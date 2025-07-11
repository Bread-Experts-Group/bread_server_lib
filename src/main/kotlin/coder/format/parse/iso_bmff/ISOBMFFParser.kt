package org.bread_experts_group.coder.format.parse.iso_bmff

import org.bread_experts_group.coder.format.parse.CodingCompoundThrowable
import org.bread_experts_group.coder.format.parse.InvalidInputException
import org.bread_experts_group.coder.format.parse.Parser
import org.bread_experts_group.coder.format.parse.iso_bmff.box.*
import org.bread_experts_group.stream.*
import java.io.InputStream
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class ISOBMFFParser(
	from: InputStream
) : Parser<String, ISOBMFFBox, InputStream>("ISO Base Media File Format", from) {
	override fun responsibleStream(of: ISOBMFFBox): InputStream = of.data.inputStream()

	override fun readBase(compound: CodingCompoundThrowable): ISOBMFFBox {
		val (name, data) = fqIn.read32ul().let { size32 ->
			val name = fqIn.readString(4)
			val size64 = if (size32 == 1L) fqIn.read64() else size32
			if (size64 > Int.MAX_VALUE) throw UnsupportedOperationException("Size is too big [${size64.toULong()}]!")
			name to
					(if (size32 == 0L) fqIn.readAllBytes()
					else fqIn.readNBytes(size64.toInt() - 8))
		}
		return ISOBMFFBox(name, data)
	}

	fun containerBox(name: String) {
		this.addParser(name) { stream, box, _ ->
			ISOBMFFContainerBox(
				box.tag,
				ISOBMFFParser(stream)
			)
		}
	}

	fun containerFullBox(name: String) {
		this.addParser(name) { stream, box, _ ->
			val (version, flags) = fullBox(stream)
			ISOBMFFContainerFullBox(
				box.tag,
				ISOBMFFParser(stream),
				version,
				flags
			)
		}
	}

	fun fullBox(stream: InputStream): Pair<Int, Int> {
		return stream.read() to stream.read24()
	}

	init {
		containerBox("moov")
		containerBox("trak")
		containerBox("mdia")
		containerBox("udta")
		containerBox("minf")
		containerBox("dinf")
		containerBox("stbl")
		containerFullBox("meta")

		fun InputStream.readLanguage(): Locale? {
			val langCode = this.read16ui().let {
				Char(((it shr 10) and 0x1F) + 0x60).toString() +
						Char(((it shr 5) and 0x1F) + 0x60) +
						Char((it and 0x1F) + 0x60)
			}
			return Locale.getISOLanguages().firstNotNullOfOrNull {
				val locale = Locale.of(it)
				if (locale.isO3Language == langCode) locale else null
			}
		}

		addParser("cprt") { stream, box, _ ->
			val (version, flags) = fullBox(stream)
			when (version) {
				0 -> ISOBMFFCopyrightBoxV0(
					flags,
					stream.readLanguage(),
					stream.readAllBytes()
						.let { it.sliceArray(0 until it.size - 1) }
						.decodeToString()
				)

				else -> throw InvalidInputException("Unsupported version: $version")
			}
		}
		addParser("ftyp") { stream, box, _ ->
			ISOBMFFFileTypeCompatibilityBox(
				stream.readString(4),
				stream.read32(),
				buildList {
					while (stream.available() > 0) add(stream.readString(4))
				}
			)
		}

		fun zonedDateTime(n: Long, y: Long): ZonedDateTime = ZonedDateTime.ofInstant(
			Instant.ofEpochSecond(n),
			ZoneId.of("UTC")
		).plusYears(y)

		addParser("mvhd") { stream, box, _ ->
			val (version, flags) = fullBox(stream)
			when (version) {
				0 -> ISOMBFFMovieHeaderBoxV0(
					flags,
					zonedDateTime(stream.read32ul(), -66),
					zonedDateTime(stream.read32ul(), -66),
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
					flags,
					zonedDateTime(stream.read64(), 70),
					zonedDateTime(stream.read64(), 70),
					stream.read32(),
					stream.read64(),
					stream.read16ui() + (stream.read16ui() / 65536.0),
					stream.read() + (stream.read() / 256.0),
					stream.readNBytes(10),
					Array(9) { stream.read32() }.toIntArray(),
					stream.readNBytes(24),
					stream.read32()
				)

				else -> throw InvalidInputException("Unsupported version: $version")
			}
		}
		addParser("mdhd") { stream, box, _ ->
			val (version, flags) = fullBox(stream)
			when (version) {
				0 -> ISOBMFFMediaHeaderFullBoxV0(
					flags,
					zonedDateTime(stream.read32ul(), -66),
					zonedDateTime(stream.read32ul(), -66),
					stream.read32(),
					stream.read32(),
					stream.readLanguage(),
					stream.read16ui()
				)

				else -> throw InvalidInputException("Unsupported version: $version")
			}
		}
		addParser("tkhd") { stream, box, _ ->
			val (version, flags) = fullBox(stream)
			when (version) {
				0 -> ISOBMFFTrackHeaderFullBoxV0(
					flags,
					zonedDateTime(stream.read32ul(), -66),
					zonedDateTime(stream.read32ul(), -66),
					stream.read32(),
					stream.read32(),
					stream.read32(),
					Array(2) { stream.read32() }.toIntArray(),
					stream.read16ui(),
					stream.read16ui(),
					stream.read() + (stream.read() / 256.0),
					stream.read16ui(),
					Array(9) { stream.read32() }.toIntArray(),
					stream.read16ui() + (stream.read16ui() / 65536.0),
					stream.read16ui() + (stream.read16ui() / 65536.0)
				)

				else -> throw InvalidInputException("Unsupported version: $version")
			}
		}
		addParser("hdlr") { stream, box, _ ->
			val (version, flags) = fullBox(stream)
			when (version) {
				0 -> ISOBMFFHandlerFullBoxV0(
					flags,
					stream.read32(),
					stream.readString(4),
					Array(3) { stream.read32() }.toIntArray(),
					stream.readAllBytes()
						.let { it.sliceArray(0 until it.size - 1) }
						.decodeToString()
				)

				else -> throw InvalidInputException("Unsupported version: $version")
			}
		}
		containerBox("ilst")
	}
}