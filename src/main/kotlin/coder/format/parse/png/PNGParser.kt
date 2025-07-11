package org.bread_experts_group.coder.format.parse.png

import org.bread_experts_group.coder.Flaggable.Companion.allPresent
import org.bread_experts_group.coder.Flaggable.Companion.from
import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.coder.SaveSingle
import org.bread_experts_group.coder.format.parse.*
import org.bread_experts_group.coder.format.parse.png.chunk.*
import org.bread_experts_group.coder.format.parse.tiff.TIFFParser
import org.bread_experts_group.hex
import org.bread_experts_group.stream.*
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.time.DateTimeException
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*
import java.util.zip.InflaterInputStream

@OptIn(ExperimentalUnsignedTypes::class)
class PNGParser(
	from: InputStream
) : Parser<String, PNGChunk, InputStream>("Portable Network Graphics", from) {
	init {
		val signature = from.readNBytes(8)
		val goodSignature = ubyteArrayOf(137u, 80u, 78u, 71u, 13u, 10u, 26u, 10u).toByteArray()
		if (!signature.contentEquals(goodSignature)) throw InvalidInputException(
			"PNG signature mismatch; [${signature.toHexString()} =/= ${goodSignature.toHexString()}]"
		)
	}

	override fun responsibleStream(of: PNGChunk): InputStream = of.data.inputStream()

	private val mustAppear = mutableSetOf("IHDR", "IDAT")
	private val unsafeToAppear = mutableSetOf("fdAT")
	private var savedNext: PNGChunk? by SaveSingle()
	private var shouldHaveEnded = false

	override fun fallbackBase(compound: CodingCompoundThrowable, of: PNGChunk, vararg parameter: Any): PNGChunk {
		if (of.tag == "IDAT" || of.tag == "IEND") return of
		if (of.critical) compound.addThrown(ValidationException("Unrecognized critical $of"))
		if (of.specification) compound.addThrown(ValidationException("Unrecognized specification $of"))
		if (of.reserved) compound.addThrown(ValidationException("Illegal reserved $of"))
		return of
	}

	private fun internalReadBase(compound: CodingCompoundThrowable): PNGChunk? {
		val length = fqIn.read32()
		if (length < 0) {
			compound.addThrown(InvalidInputException("Corrupted stream [$length]"))
			rawStream.skipNBytes(rawStream.available().toLong())
			return null
		}
		val chunkTypeRaw = fqIn.readNBytes(4)
		val chunkType = chunkTypeRaw.toString(Charsets.ISO_8859_1)
		if (mustAppear.contains("IHDR") && chunkType != "IHDR") compound.addThrown(
			ValidationException("\"$chunkType\" came before \"IHDR\"")
		) else mustAppear.remove("IHDR")
		if (unsafeToAppear.contains(chunkType)) compound.addThrown(
			ValidationException("\"$chunkType\" reappeared where it was not allowed; bad: $unsafeToAppear")
		)
		if (shouldHaveEnded) compound.addThrown(
			ValidationException("\"$chunkType\" came after \"IEND\"")
		)
		val chunkData = fqIn.readNBytes(length)
		val crc32 = fqIn.read32ul()
		val computedCRC32 = (chunkTypeRaw + chunkData).crc32()
		if (crc32 != computedCRC32) compound.addThrown(
			ValidationException(
				"CRC32 incorrect; [${hex(crc32.toUInt())} =/= ${hex(computedCRC32.toUInt())}]"
			)
		)
		return PNGChunk(chunkType, chunkData)
	}

	override fun readBase(compound: CodingCompoundThrowable): PNGChunk? = savedNext ?: run {
		var base = internalReadBase(compound)
		if (base == null) return@run null
		when (base.tag) {
			"iCCP", "sRGB" -> unsafeToAppear.addAll(arrayOf("iCCP", "sRGB"))
			"PLTE" -> unsafeToAppear.addAll(
				arrayOf("PLTE", "cHRM", "cICP", "gAMA", "iCCP", "mDCV", "cLLI", "sBIT", "sRGB")
			)

			"IHDR", "acTL", "cHRM", "cICP", "gAMA", "mDCV", "cLLI", "sBIT",
			"bKGD", "hIST", "tRNS", "eXIf", "pHYs", "tIME" -> unsafeToAppear.add(base.tag)

			"IDAT", "fdAT" -> {
				mustAppear.remove("IDAT")
				unsafeToAppear.remove("fdAT")
				val internalComposite = ByteArrayOutputStream(base.data.size)
				internalComposite.write(base.data)
				do {
					val next = internalReadBase(compound)
					if (next == null) return@run null
					if (next.tag != base.tag) {
						savedNext = next
						break
					}
					internalComposite.write(next.data)
				} while (next.tag == base.tag)
				if (base.tag == "IDAT") unsafeToAppear.addAll(
					arrayOf("IDAT", "acTL", "bKGD", "hIST", "tRNS", "eXIf", "pHYs", "sPLT")
				)
				base = PNGChunk(base.tag, internalComposite.toByteArray())
			}

			"IEND" -> {
				unsafeToAppear.add(base.tag)
				shouldHaveEnded = true
				for (dA in mustAppear) compound.addThrown(ValidationException("\"$dA\" never appeared"))
			}
		}
		return base
	}

	private lateinit var header: PNGHeaderChunk
	private lateinit var palette: PNGPaletteChunk

	init {
		addParser("IHDR") { stream, chunk, compound ->
			val width = stream.read32()
			val height = stream.read32()
			val bitDepth = stream.read()
			val colorType = stream.read()
			when (colorType) {
				0 -> when (bitDepth) {
					1, 2, 4, 8, 16 -> {}
					else -> compound.addThrown(InvalidInputException("Bad depth [Grayscale] [$bitDepth]"))
				}

				2 -> when (bitDepth) {
					8, 16 -> {}
					else -> compound.addThrown(InvalidInputException("Bad depth [RGB] [$bitDepth]"))
				}

				3 -> when (bitDepth) {
					1, 2, 4, 8 -> {}
					else -> compound.addThrown(InvalidInputException("Bad depth [Palette] [$bitDepth]"))
				}

				4 -> when (bitDepth) {
					8, 16 -> {}
					else -> compound.addThrown(InvalidInputException("Bad depth [Grayscale (Alpha)] [$bitDepth]"))
				}

				6 -> when (bitDepth) {
					8, 16 -> {}
					else -> compound.addThrown(InvalidInputException("Bad depth [RGB (Alpha)] [$bitDepth]"))
				}

				else -> compound.addThrown(UnsupportedValueException("PNG color type [$colorType]"))
			}
			header = PNGHeaderChunk(
				width, height, bitDepth,
				PNGHeaderFlags.entries.from(colorType),
				PNGCompressionType.entries.id(stream.read()),
				PNGFilterType.entries.id(stream.read()),
				PNGInterlaceType.entries.id(stream.read())
			)
			header
		}
		addParser("PLTE") { stream, _, _ ->
			palette = PNGPaletteChunk(
				buildList {
					while (stream.available() > 0) add(Color(stream.read(), stream.read(), stream.read()))
				}
			)
			palette
		}
		addParser("sPLT") { stream, _, compound ->
			PNGSuggestedPaletteChunk(
				stream.readString(Charsets.ISO_8859_1),
				buildList {
					when (val depth = stream.read()) {
						8 -> while (stream.available() > 0) add(
							Color(
								stream.read() / 255f,
								stream.read() / 255f,
								stream.read() / 255f,
								stream.read() / 255f
							) to stream.read16ui()
						)

						16 -> while (stream.available() > 0) add(
							Color(
								stream.read16ui() / 65535f,
								stream.read16ui() / 65535f,
								stream.read16ui() / 65535f,
								stream.read16ui() / 65535f
							) to stream.read16ui()
						)

						else -> compound.addThrown(InvalidInputException("Invalid sPLT depth [$depth]"))
					}
				}
			)
		}
		addParser("acTL") { stream, _, _ ->
			PNGAnimationControlChunk(
				stream.read32(),
				stream.read32()
			)
		}
		addParser("fcTL") { stream, _, _ ->
			val sequence = stream.read32()
			val width = stream.read32()
			val height = stream.read32()
			val x = stream.read32()
			val y = stream.read32()
			val numerator = stream.read16ui()
			val denominator = stream.read16ui().let { if (it == 0) 100 else it }
			val delayMillis =
				if (numerator == 0) 0L
				else ((numerator.toDouble() / denominator) * 1000L).toLong()
			PNGFrameControlChunk(
				sequence, width, height, x, y,
				delayMillis,
				PNGDisposeOperation.entries.id(stream.read()),
				PNGBlendOperation.entries.id(stream.read())
			)
		}
		addParser("fdAT") { stream, _, _ ->
			PNGFrameDataChunk(
				stream.read32(),
				stream.readAllBytes()
			)
		}
		addParser("gAMA") { stream, _, _ ->
			PNGGammaChunk(stream.read32ul() / 100000.0)
		}
		addParser("sBIT") { stream, chunk, compound ->
			if (!this::header.isInitialized) {
				compound.addThrown(RefinementException("IHDR not defined"))
				return@addParser chunk
			}

			PNGSignificantBitsChunk(
				ByteArrayOutputStream().use { out ->
					out.write(stream.read()) // R / W
					if (header.flags.allPresent(PNGHeaderFlags.TRUE_COLOR)) {
						out.write(stream.read()) // G
						out.write(stream.read()) // B
					}
					if (header.flags.allPresent(PNGHeaderFlags.ALPHA))
						out.write(stream.read()) // A
					out.toByteArray()
				}
			)
		}
		addParser("bKGD") { stream, chunk, compound ->
			if (!this::header.isInitialized) {
				compound.addThrown(RefinementException("IHDR not defined"))
				return@addParser chunk
			}

			val mask = header.bitDepth.maskI()
			PNGBackgroundChunk(
				when {
					header.flags.allPresent(PNGHeaderFlags.PALETTE) -> {
						if (!this::palette.isInitialized) {
							compound.addThrown(RefinementException("PLTE not defined"))
							return@addParser chunk
						}
						val index = stream.read()
						if (index !in palette.colors.indices) {
							compound.addThrown(RefinementException("[$index] out of range in PLTE"))
							return@addParser chunk
						}
						palette.colors[index]
					}

					header.flags.allPresent(PNGHeaderFlags.TRUE_COLOR) -> Color(
						(stream.read16ui() and mask) / (mask.toFloat()),
						(stream.read16ui() and mask) / (mask.toFloat()),
						(stream.read16ui() and mask) / (mask.toFloat())
					)

					else -> {
						val gray = (stream.read16ui() and mask) / (mask.toFloat())
						Color(gray, gray, gray)
					}
				}
			)
		}
		addParser("tRNS") { stream, chunk, compound ->
			if (!this::header.isInitialized) {
				compound.addThrown(RefinementException("IHDR not defined"))
				return@addParser chunk
			}

			if (header.flags.allPresent(PNGHeaderFlags.PALETTE)) PNGTransparencyPaletteChunk(
				buildList {
					while (stream.available() > 0) {
						add(stream.read() / 255f)
					}
				}
			) else {
				val mask = header.bitDepth.maskI()
				PNGTransparencySingleChunk(
					when {
						header.flags.allPresent(PNGHeaderFlags.TRUE_COLOR) -> Color(
							(stream.read16ui() and mask) / (mask.toFloat()),
							(stream.read16ui() and mask) / (mask.toFloat()),
							(stream.read16ui() and mask) / (mask.toFloat())
						)

						else -> {
							val gray = (stream.read16ui() and mask) / (mask.toFloat())
							Color(gray, gray, gray)
						}
					}
				)
			}
		}
		addParser("cHRM") { stream, _, _ ->
			PNGChromaticitiesChunk(
				Point2D(stream.read32() / 100000.0, stream.read32() / 100000.0),
				Point2D(stream.read32() / 100000.0, stream.read32() / 100000.0),
				Point2D(stream.read32() / 100000.0, stream.read32() / 100000.0),
				Point2D(stream.read32() / 100000.0, stream.read32() / 100000.0)
			)
		}
		addParser("pHYs") { stream, _, _ ->
			PNGPhysicalPixelDimensionsChunk(
				stream.read32(),
				stream.read32(),
				PNGPixelDimensions.entries.id(stream.read())
			)
		}
		addParser("hIST") { stream, _, _ ->
			PNGHistogramChunk(
				buildList {
					while (stream.available() > 0) add(stream.read16ui())
				}
			)
		}
		addParser("tIME") { stream, chunk, compound ->
			try {
				PNGTimestampChunk(
					ZonedDateTime.of(
						stream.read16ui(),
						stream.read(),
						stream.read(),
						stream.read(),
						stream.read(),
						stream.read(),
						0,
						ZoneOffset.UTC
					)
				)
			} catch (e: DateTimeException) {
				compound.addThrown(RefinementException("tIME parsing", e))
				chunk
			}
		}
		addParser("tEXt") { stream, _, _ ->
			PNGTextChunk(
				stream.readString(Charsets.ISO_8859_1),
				stream.readString(Charsets.ISO_8859_1)
			)
		}
		addParser("zTXt") { stream, _, compound ->
			val keyword = stream.readString(Charsets.ISO_8859_1)
			val compressionMethod = PNGCompressionType.entries.id(stream.read())
			PNGCompressedTextChunk(
				keyword, compressionMethod,
				when (compressionMethod) {
					PNGCompressionType.DEFLATE -> try {
						FailQuickInputStream(
							InflaterInputStream(stream.readAllBytes().inputStream())
						).use { it.readString(Charsets.ISO_8859_1) }
					} catch (e: IOException) {
						compound.addThrown(InvalidInputException("Decompression failure", e))
						""
					}

					else -> {
						compound.addThrown(
							InvalidInputException("Compression type for zTXt [$compressionMethod]")
						)
						""
					}
				}
			)
		}
		addParser("iTXt") { stream, _, compound ->
			val keyword = stream.readString(Charsets.ISO_8859_1)
			val compressionMethod =
				if (stream.read() == 1) PNGCompressionType.entries.id(stream.read())
				else {
					stream.read()
					null
				}
			PNGInternationalTextChunk(
				keyword,
				compressionMethod,
				Locale.forLanguageTag(stream.readString(Charsets.ISO_8859_1)),
				stream.readString(Charsets.UTF_8),
				when (compressionMethod) {
					PNGCompressionType.DEFLATE -> try {
						FailQuickInputStream(
							InflaterInputStream(stream.readAllBytes().inputStream())
						).use { it.readString(Charsets.UTF_8) }
					} catch (e: IOException) {
						compound.addThrown(InvalidInputException("Decompression failure", e))
						""
					}

					null -> stream.readString(Charsets.UTF_8)
					else -> {
						compound.addThrown(
							InvalidInputException("Compression type for iTXt [$compressionMethod]")
						)
						""
					}
				}
			)
		}
		addParser("eXIf") { stream, _, _ ->
			PNGEXIFChunk(TIFFParser(stream.readAllBytes()))
		}
		addParser("iCCP") { stream, _, compound ->
			val profileName = stream.readString(Charsets.ISO_8859_1)
			val compressionMethod = PNGCompressionType.entries.id(stream.read())
			PNGEmbeddedICCChunk(
				profileName,
				when (compressionMethod) {
					PNGCompressionType.DEFLATE -> try {
						FailQuickInputStream(
							InflaterInputStream(stream.readAllBytes().inputStream())
						).use { it.readAllBytes() }
					} catch (e: IOException) {
						compound.addThrown(InvalidInputException("Decompression failure", e))
						byteArrayOf()
					}

					else -> {
						compound.addThrown(
							InvalidInputException("Compression type for iCCP [$compressionMethod]")
						)
						byteArrayOf()
					}
				}
			)
		}
		addParser("sRGB") { stream, _, _ ->
			PNGSRGBChunk(PNGSRGBIntent.entries.id(stream.read()))
		}
		addParser("cICP") { stream, _, _ ->
			PNGICPVSTIDChunk(
				stream.read(),
				stream.read(),
				stream.read(),
				stream.read()
			)
		}
		addParser("mDCV") { stream, _, _ ->
			PNGMasteringDisplayColorVolumeChunk(
				stream.readNBytes(12),
				stream.read32ul() * 0.00002,
				stream.read32ul() * 0.0001,
				stream.read32ul() * 0.0001
			)
		}
		addParser("cLLI") { stream, _, _ ->
			PNGContentLightLevelInfoChunk(
				stream.read32ul() * 0.0001,
				stream.read32ul() * 0.0001
			)
		}
	}
}