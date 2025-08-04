package org.bread_experts_group.coder.format.parse.png

import org.bread_experts_group.channel.*
import org.bread_experts_group.coder.Flaggable.Companion.from
import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.coder.SaveSingle
import org.bread_experts_group.coder.format.parse.*
import org.bread_experts_group.coder.format.parse.gif.GIFDisposalMethod
import org.bread_experts_group.coder.format.parse.png.chunk.*
import org.bread_experts_group.coder.format.parse.tiff.TIFFByteParser
import org.bread_experts_group.hex
import org.bread_experts_group.numeric.geometry.Point2D
import org.bread_experts_group.stream.*
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.io.EOFException
import java.io.IOException
import java.math.BigDecimal
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.channels.SeekableByteChannel
import java.time.DateTimeException
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*
import java.util.zip.CRC32
import java.util.zip.InflaterInputStream
import kotlin.math.min
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@OptIn(ExperimentalUnsignedTypes::class)
class PNGByteParser : ByteParser<String, PNGChunk, SeekableByteChannel>(
	"Portable Network Graphics"
) {
	override fun responsibleChannel(of: PNGChunk): SeekableByteChannel = of.window
	private val mustAppear = mutableSetOf<String>()
	private val unsafeToAppear = mutableSetOf<String>()
	private var savedNext: PNGChunk? by SaveSingle()
	private var shouldHaveEnded = false

	override fun isUnknown(of: PNGChunk): Boolean = of.specification
	override fun fallbackBase(compound: CodingCompoundThrowable, of: PNGChunk, vararg parameter: Any): PNGChunk {
		if (of.tag == "IDAT" || of.tag == "IEND") return of
		if (of.critical) compound.addThrown(ValidationException("Unrecognized critical $of"))
		if (of.specification) compound.addThrown(ValidationException("Unrecognized specification $of"))
		if (of.reserved) compound.addThrown(ValidationException("Illegal reserved $of"))
		return of
	}

	private val array4 = ByteArray(4)
	private val buffer12 = ByteBuffer.allocate(12)
	private val crc32Buffer = ByteBuffer.allocate(4096)
	private fun internalReadBase(compound: CodingCompoundThrowable): PNGChunk? {
		buffer12.limit(8)
		buffer12.rewind()
		channel.read(buffer12)
		buffer12.rewind()
		val length = buffer12.int
		if (length < 0 || length > channel.position() + channel.size()) {
			compound.addThrown(InvalidInputException("Corrupted stream [$length]"))
			return null
		}
		val crc32 = CRC32()
		buffer12.get(array4)
		crc32.update(array4)
		val chunkType = array4.toString(Charsets.ISO_8859_1)
		if (mustAppear.contains("IHDR") && chunkType != "IHDR") compound.addThrown(
			ValidationException("\"$chunkType\" came before \"IHDR\"")
		) else mustAppear.remove("IHDR")
		if (unsafeToAppear.contains(chunkType)) compound.addThrown(
			ValidationException("\"$chunkType\" reappeared where it was not allowed; bad: $unsafeToAppear")
		)
		if (shouldHaveEnded) compound.addThrown(
			ValidationException("\"$chunkType\" came after \"IEND\"")
		)
		val from = channel.position()
		var computed = 0
		while (computed < length) {
			crc32Buffer.limit(min(crc32Buffer.capacity(), length - computed))
			crc32Buffer.rewind()
			val read = channel.read(crc32Buffer)
			if (read == -1) throw EOFException()
			crc32.update(crc32Buffer.array(), 0, read)
			computed += read
		}
		buffer12.limit(12)
		channel.read(buffer12)
		buffer12.position(8)
		val chunkCRC32 = buffer12.int.toLong() and 0xFFFFFFFF
		val computedCRC32 = crc32.value
		if (chunkCRC32 != computedCRC32) compound.addThrown(
			ValidationException(
				"CRC32 incorrect; [${hex(chunkCRC32.toUInt())} =/= ${hex(computedCRC32.toUInt())}]"
			)
		)
		buffer12.limit(8)
		buffer12.rewind()
		return PNGChunk(
			chunkType,
			WindowedSeekableByteChannel(channel, from, from + length)
		)
	}

	override fun readBase(compound: CodingCompoundThrowable): PNGChunk? = savedNext ?: run {
		if (channel.position() == channel.size()) throw EOFException()
		var base = internalReadBase(compound)
		if (base == null) return@run null
		when (base.tag) {
			"iCCP", "sRGB" -> unsafeToAppear.addAll(arrayOf("iCCP", "sRGB"))
			"PLTE" -> unsafeToAppear.addAll(
				arrayOf("PLTE", "cHRM", "cICP", "gAMA", "iCCP", "mDCV", "cLLI", "sBIT", "sRGB")
			)

			"IHDR", "acTL", "cHRM", "cICP", "gAMA", "mDCV", "cLLI", "sBIT",
			"bKGD", "hIST", "tRNS", "eXIf", "pHYs", "tIME" -> unsafeToAppear.add(base.tag)

			"IDAT" -> {
				unsafeToAppear.remove("fdAT")
				val windows = mutableListOf(base.window)
				while (true) {
					val next = internalReadBase(compound)
					if (next == null) return@run null
					if (next.tag != base.tag || channel.position() == channel.size()) {
						savedNext = next
						break
					}
					windows.add(next.window)
				}
				mustAppear.remove("IDAT")
				unsafeToAppear.add("IDAT")
				base = PNGDataChunk(windows)
			}

			"fdAT" -> {
				val seqBuffer = ByteBuffer.allocate(4)
				base.window.read(seqBuffer)
				seqBuffer.rewind()
				val windows = mutableListOf(base.window)
				while (true) {
					val next = internalReadBase(compound)
					if (next == null) return@run null
					if (next.tag != base.tag) {
						savedNext = next
						break
					}
					windows.add(
						WindowedSeekableByteChannel(
							channel,
							(next.window as WindowedSeekableByteChannel).start + 4,
							next.window.end
						)
					)
				}
				base = PNGFrameDataChunk(seqBuffer.int, windows)
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
		addParser("IHDR") { channel, chunk, compound ->
			val bufferIHDR = ByteBuffer.allocate(13)
			channel.read(bufferIHDR)
			bufferIHDR.rewind()
			val width = bufferIHDR.int
			val height = bufferIHDR.int
			val bitDepth = bufferIHDR.byteInt
			val colorType = bufferIHDR.byteInt
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
				PNGCompressionType.entries.id(bufferIHDR.byteInt).enum,
				PNGFilterType.entries.id(bufferIHDR.byteInt).enum,
				PNGInterlaceType.entries.id(bufferIHDR.byteInt).enum,
				channel
			)
			header
		}
		addParser("PLTE") { channel, _, _ ->
			val bufferPLTE = ByteBuffer.allocate(4)
			palette = PNGPaletteChunk(
				List((channel.size() / 3).toInt()) {
					bufferPLTE.position(1)
					channel.read(bufferPLTE)
					bufferPLTE.rewind()
					Color(bufferPLTE.int)
				},
				channel
			)
			palette
		}
		addParser("sPLT") { channel, chunk, compound ->
			// TODO: Make ByteBuffers in general use ReadingByteBuffers
			val reader = ReadingByteBuffer(channel, ByteBuffer.allocate(100), null)
			PNGSuggestedPaletteChunk(
				reader.decodeString(Charsets.ISO_8859_1),
				buildList {
					val depth = reader.u8i32()
					try {
						while (true) {
							when (depth) {
								8 -> add(
									Color(
										reader.u8i32() / 255f,
										reader.u8i32() / 255f,
										reader.u8i32() / 255f,
										reader.u8i32() / 255f
									) to reader.u16i32()
								)

								16 -> add(
									Color(
										reader.u16i32() / 65535f,
										reader.u16i32() / 65535f,
										reader.u16i32() / 65535f,
										reader.u16i32() / 65535f
									) to reader.u16i32()
								)

								else -> {
									compound.addThrown(InvalidInputException("Invalid sPLT depth [$depth]"))
									return@addParser chunk
								}
							}
						}
					} catch (_: EOFException) {
					}
				},
				channel
			)
		}
		addParser("acTL") { channel, _, _ ->
			val bufferACTL = ByteBuffer.allocate(8)
			channel.read(bufferACTL)
			bufferACTL.rewind()
			PNGAnimationControlChunk(
				bufferACTL.int,
				bufferACTL.int,
				channel
			)
		}
		addParser("fcTL") { channel, _, _ ->
			val bufferFCTL = ByteBuffer.allocate(26)
			channel.read(bufferFCTL)
			bufferFCTL.rewind()
			val sequence = bufferFCTL.int
			val width = bufferFCTL.int
			val height = bufferFCTL.int
			val x = bufferFCTL.int
			val y = bufferFCTL.int
			val numerator = bufferFCTL.shortInt
			val denominator = bufferFCTL.shortInt.let { if (it == 0) 100 else it }
			val delayMillis =
				if (numerator == 0) 0L
				else ((numerator.toDouble() / denominator) * 1000L).toLong()
			PNGFrameControlChunk(
				sequence, width, height, x, y,
				delayMillis.toDuration(DurationUnit.MILLISECONDS),
				PNGDisposeOperation.entries.id(bufferFCTL.byteInt).enum,
				PNGBlendOperation.entries.id(bufferFCTL.byteInt).enum,
				channel
			)
		}
		addParser("gAMA") { channel, _, _ ->
			val bufferGAMA = ByteBuffer.allocate(4)
			channel.read(bufferGAMA)
			bufferGAMA.rewind()
			PNGGammaChunk(
				bufferGAMA.intLong / 100000.0,
				channel
			)
		}
		addParser("sBIT") { channel, chunk, compound ->
			val bufferSBIT = ByteBuffer.allocate(4)
			channel.read(bufferSBIT)
			bufferSBIT.rewind()
			if (!this::header.isInitialized) {
				compound.addThrown(RefinementException("IHDR not defined"))
				return@addParser chunk
			}

			PNGSignificantBitsChunk(
				ByteArrayOutputStream().use { out ->
					out.write(bufferSBIT.byteInt) // R / W
					if (header.flags.contains(PNGHeaderFlags.TRUE_COLOR)) {
						out.write(bufferSBIT.byteInt) // G
						out.write(bufferSBIT.byteInt) // B
					}
					if (header.flags.contains(PNGHeaderFlags.ALPHA))
						out.write(bufferSBIT.byteInt) // A
					out.toByteArray()
				},
				channel
			)
		}
		addParser("bKGD") { channel, chunk, compound ->
			val bufferBKGD = ByteBuffer.allocate(6)
			channel.read(bufferBKGD)
			bufferBKGD.rewind()
			if (!this::header.isInitialized) {
				compound.addThrown(RefinementException("IHDR not defined"))
				return@addParser chunk
			}

			val mask = header.bitDepth.maskI()
			PNGBackgroundChunk(
				when {
					header.flags.contains(PNGHeaderFlags.PALETTE) -> {
						if (!this::palette.isInitialized) {
							compound.addThrown(RefinementException("PLTE not defined"))
							return@addParser chunk
						}
						val index = bufferBKGD.byteInt
						if (index !in palette.colors.indices) {
							compound.addThrown(RefinementException("[$index] out of range in PLTE"))
							return@addParser chunk
						}
						palette.colors[index]
					}

					header.flags.contains(PNGHeaderFlags.TRUE_COLOR) -> Color(
						(bufferBKGD.shortInt and mask) / (mask.toFloat()),
						(bufferBKGD.shortInt and mask) / (mask.toFloat()),
						(bufferBKGD.shortInt and mask) / (mask.toFloat())
					)

					else -> {
						val gray = (bufferBKGD.shortInt and mask) / (mask.toFloat())
						Color(gray, gray, gray)
					}
				},
				channel
			)
		}
		addParser("tRNS") { channel, chunk, compound ->
			val stream = Channels.newInputStream(channel)
			if (!this::header.isInitialized) {
				compound.addThrown(RefinementException("IHDR not defined"))
				return@addParser chunk
			}

			if (header.flags.contains(PNGHeaderFlags.PALETTE)) PNGTransparencyPaletteChunk(
				buildList {
					while (stream.available() > 0) {
						add(stream.read() / 255f)
					}
				},
				channel
			) else {
				val mask = header.bitDepth.maskI()
				PNGTransparencySingleChunk(
					when {
						header.flags.contains(PNGHeaderFlags.TRUE_COLOR) -> Color(
							(stream.read16ui() and mask) / (mask.toFloat()),
							(stream.read16ui() and mask) / (mask.toFloat()),
							(stream.read16ui() and mask) / (mask.toFloat())
						)

						else -> {
							val gray = (stream.read16ui() and mask) / (mask.toFloat())
							Color(gray, gray, gray)
						}
					},
					channel
				)
			}
		}
		addParser("cHRM") { channel, _, _ ->
			val bufferCHRM = ByteBuffer.allocate(32)
			channel.read(bufferCHRM)
			bufferCHRM.rewind()
			PNGChromaticitiesChunk(
				Point2D(bufferCHRM.intLong / 100000.0, bufferCHRM.intLong / 100000.0),
				Point2D(bufferCHRM.intLong / 100000.0, bufferCHRM.intLong / 100000.0),
				Point2D(bufferCHRM.intLong / 100000.0, bufferCHRM.intLong / 100000.0),
				Point2D(bufferCHRM.intLong / 100000.0, bufferCHRM.intLong / 100000.0),
				channel
			)
		}
		addParser("pHYs") { channel, _, _ ->
			val bufferPHYS = ByteBuffer.allocate(9)
			channel.read(bufferPHYS)
			bufferPHYS.rewind()
			PNGPhysicalPixelDimensionsChunk(
				bufferPHYS.intLong,
				bufferPHYS.intLong,
				PNGPixelDimensions.entries.id(bufferPHYS.byteInt).enum,
				channel
			)
		}
		addParser("hIST") { channel, _, _ ->
			val stream = Channels.newInputStream(channel)
			PNGHistogramChunk(
				buildList {
					while (stream.available() > 0) add(stream.read16ui())
				},
				channel
			)
		}
		addParser("tIME") { channel, chunk, compound ->
			val bufferTIME = ByteBuffer.allocate(7)
			channel.read(bufferTIME)
			bufferTIME.rewind()
			try {
				PNGTimestampChunk(
					ZonedDateTime.of(
						bufferTIME.shortInt,
						bufferTIME.byteInt,
						bufferTIME.byteInt,
						bufferTIME.byteInt,
						bufferTIME.byteInt,
						bufferTIME.byteInt,
						0,
						ZoneOffset.UTC
					),
					channel
				)
			} catch (e: DateTimeException) {
				compound.addThrown(RefinementException("tIME parsing", e))
				chunk
			}
		}
		addParser("tEXt") { channel, _, _ ->
			val stream = FailQuickInputStream(Channels.newInputStream(channel))
			PNGTextChunk(
				stream.readString(Charsets.ISO_8859_1),
				try {
					stream.readString(Charsets.ISO_8859_1)
				} catch (_: FailQuickInputStream.EndOfStream) {
					""
				},
				channel
			)
		}
		addParser("zTXt") { channel, _, compound ->
			val stream = Channels.newInputStream(channel)
			val keyword = stream.readString(Charsets.ISO_8859_1)
			val compressionMethod = PNGCompressionType.entries.id(stream.read()).enum
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
				},
				channel
			)
		}
		addParser("iTXt") { channel, _, compound ->
			val stream = FailQuickInputStream(Channels.newInputStream(channel))
			val keyword = stream.readString(Charsets.ISO_8859_1)
			val compressionMethod =
				if (stream.read() == 1) PNGCompressionType.entries.id(stream.read()).enum
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
				},
				channel
			)
		}
		addParser("eXIf") { channel, _, _ ->
			PNGEXIFChunk(
				TIFFByteParser().setInput(channel),
				channel
			)
		}
		addParser("iCCP") { channel, _, compound ->
			val stream = Channels.newInputStream(channel)
			val profileName = stream.readString(Charsets.ISO_8859_1)
			val compressionMethod = PNGCompressionType.entries.id(stream.read()).enum
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
				},
				channel
			)
		}
		addParser("sRGB") { channel, _, _ ->
			val bufferSRGB = ByteBuffer.allocate(1)
			channel.read(bufferSRGB)
			bufferSRGB.rewind()
			PNGSRGBChunk(
				PNGSRGBIntent.entries.id(bufferSRGB.byteInt).enum,
				channel
			)
		}
		addParser("cICP") { channel, _, _ ->
			val bufferCICP = ByteBuffer.allocate(4)
			channel.read(bufferCICP)
			bufferCICP.rewind()
			PNGICPVSTIDChunk(
				PNGICPVSTIDColorPrimaries.entries.id(bufferCICP.byteInt).enum,
				PNGICPVSTIDTransferFunction.entries.id(bufferCICP.byteInt).enum,
				PNGICPVSTIDMatrixCoefficients.entries.id(bufferCICP.byteInt).enum,
				bufferCICP.byteInt != 0,
				channel
			)
		}
		addParser("mDCV") { channel, _, _ ->
			val bufferMDCV = ByteBuffer.allocate(24)
			channel.read(bufferMDCV)
			bufferMDCV.rewind()
			PNGMasteringDisplayColorVolumeChunk(
				Point2D(bufferMDCV.shortInt * 0.00002, bufferMDCV.shortInt * 0.00002),
				Point2D(bufferMDCV.shortInt * 0.00002, bufferMDCV.shortInt * 0.00002),
				Point2D(bufferMDCV.shortInt * 0.00002, bufferMDCV.shortInt * 0.00002),
				Point2D(bufferMDCV.shortInt * 0.00002, bufferMDCV.shortInt * 0.00002),
				bufferMDCV.intLong * 0.0001,
				bufferMDCV.intLong * 0.0001,
				channel
			)
		}
		addParser("cLLI") { channel, _, _ ->
			val bufferCLLI = ByteBuffer.allocate(8)
			channel.read(bufferCLLI)
			bufferCLLI.rewind()
			PNGContentLightLevelInfoChunk(
				bufferCLLI.intLong * 0.0001,
				bufferCLLI.intLong * 0.0001,
				channel
			)
		}
		// Extensions [https://w3c.github.io/png/extensions/Overview.html]
		addParser("sTER") { channel, _, _ ->
			val bufferSTER = ByteBuffer.allocate(1)
			channel.read(bufferSTER)
			bufferSTER.rewind()
			PNGStereoscopyChunk(
				PNGStereoscopyMode.entries.id(bufferSTER.byteInt).enum,
				channel
			)
		}
		addParser("oFFs") { channel, _, _ ->
			val bufferOFFS = ByteBuffer.allocate(9)
			channel.read(bufferOFFS)
			bufferOFFS.rewind()
			PNGPrintOffsetChunk(
				bufferOFFS.intLong,
				bufferOFFS.intLong,
				PNGPrintOffsetUnit.entries.id(bufferOFFS.byteInt).enum,
				channel
			)
		}
		addParser("sCAL") { channel, _, _ ->
			val stream = FailQuickInputStream(Channels.newInputStream(channel))
			PNGSubjectPhysicalScaleChunk(
				PNGSubjectPhysicalScaleUnit.entries.id(stream.read()).enum,
				BigDecimal(stream.readString(Charsets.US_ASCII)),
				BigDecimal(stream.readString(Charsets.US_ASCII)),
				channel
			)
		}
		addParser("pCAL") { channel, _, _ ->
			val stream = FailQuickInputStream(Channels.newInputStream(channel))
			val calibrationName = stream.readString(Charsets.ISO_8859_1)
			val x0 = stream.read32()
			val x1 = stream.read32()
			val equation = PNGPixelCalibrationEquationType.entries.id(stream.read()).enum
			val parameters = stream.read()
			val unitName = stream.readString(Charsets.ISO_8859_1)
			PNGPixelCalibrationChunk(
				calibrationName, x0, x1, equation,
				unitName, List(parameters) {
					BigDecimal(stream.readString(Charsets.US_ASCII))
				},
				channel
			)
		}
		addParser("gIFg") { channel, _, _ ->
			val bufferGIFG = ByteBuffer.allocate(4)
			channel.read(bufferGIFG)
			bufferGIFG.rewind()
			PNGGIFGraphicControlExtensionChunk(
				GIFDisposalMethod.entries.id(bufferGIFG.byteInt).enum,
				bufferGIFG.byteInt != 0,
				(bufferGIFG.shortInt * 10).toDuration(DurationUnit.MILLISECONDS),
				channel
			)
		}
		addParser("gIFx") { channel, _, _ ->
			val stream = FailQuickInputStream(Channels.newInputStream(channel))
			PNGGIFApplicationExtension(
				stream.readString(8, Charsets.US_ASCII),
				stream.readString(3, Charsets.US_ASCII),
				channel
			)
		}
		// Pseudo Chunks
		addParser("IDAT") { _, chunk, _ -> chunk }
		addParser("fdAT") { _, chunk, _ -> chunk }
		addParser("IEND") { _, chunk, _ -> chunk }
	}

	override fun inputInit() {
		mustAppear.clear()
		mustAppear.addAll(arrayOf("IHDR", "IDAT"))
		unsafeToAppear.clear()
		unsafeToAppear.add("fdAT")
		savedNext = null
		shouldHaveEnded = false
		val buffer8 = ByteBuffer.allocate(8)
		channel.read(buffer8)
		val wrapped = buffer8.array()
		val goodSignature = ubyteArrayOf(137u, 80u, 78u, 71u, 13u, 10u, 26u, 10u).toByteArray()
		if (!wrapped.contentEquals(goodSignature)) throw InvalidInputException(
			"PNG signature mismatch; [${wrapped.toHexString()} =/= ${goodSignature.toHexString()}]"
		)
	}
}