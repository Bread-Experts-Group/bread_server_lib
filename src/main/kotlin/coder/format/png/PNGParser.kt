package org.bread_experts_group.coder.format.png

import org.bread_experts_group.coder.format.Parser
import org.bread_experts_group.coder.format.png.chunk.*
import org.bread_experts_group.stream.read16ui
import org.bread_experts_group.stream.read32
import org.bread_experts_group.stream.readString
import java.awt.Color
import java.io.InputStream

@OptIn(ExperimentalUnsignedTypes::class, ExperimentalStdlibApi::class)
class PNGParser(from: InputStream) : Parser<String, PNGChunk, InputStream>("Portable Network Graphics", from) {
	init {
		val signature = from.readNBytes(8)
		val goodSignature = ubyteArrayOf(137u, 80u, 78u, 71u, 13u, 10u, 26u, 10u).toByteArray()
		require(signature.contentEquals(goodSignature)) {
			"PNG signature mismatch; [${signature.toHexString()} =/= ${goodSignature.toHexString()}]"
		}
	}

	override fun responsibleStream(of: PNGChunk): InputStream = of.data.inputStream()

	override fun readBase(): PNGChunk {
		val length = fqIn.read32()
		val chunkType = fqIn.readString(4)
		val chunkData = fqIn.readNBytes(length)
		fqIn.readNBytes(4)
		return PNGChunk(chunkType, chunkData)
	}

	init {
		addParser("IHDR") { stream, chunk ->
			val width = stream.read32()
			val height = stream.read32()
			val bitDepth = stream.read()
			val rawColorType = stream.read()
			when (rawColorType) {
				0 -> when (bitDepth) {
					1, 2, 4, 8, 16 -> {}
					else -> throw IllegalArgumentException("Bad depth [Grayscale] [$bitDepth]")
				}

				2 -> when (bitDepth) {
					8, 16 -> {}
					else -> throw IllegalArgumentException("Bad depth [RGB] [$bitDepth]")
				}

				3 -> when (bitDepth) {
					1, 2, 4, 8 -> {}
					else -> throw IllegalArgumentException("Bad depth [Palette] [$bitDepth]")
				}

				4 -> when (bitDepth) {
					8, 16 -> {}
					else -> throw IllegalArgumentException("Bad depth [Grayscale (Alpha)] [$bitDepth]")
				}

				6 -> when (bitDepth) {
					8, 16 -> {}
					else -> throw IllegalArgumentException("Bad depth [RGB (Alpha)] [$bitDepth]")
				}

				else -> throw UnsupportedOperationException("PNG color type [$rawColorType]")
			}
			PNGHeaderChunk(
				width, height, bitDepth,
				rawColorType and 1 == 1,
				rawColorType and 2 == 2,
				rawColorType and 4 == 4,
				PNGCompressionType.mapping.getValue(stream.read()),
				PNGFilterType.mapping.getValue(stream.read()),
				PNGInterlaceType.mapping.getValue(stream.read())
			)
		}
		addParser("PLTE") { stream, chunk ->
			PNGPaletteChunk(
				buildList {
					while (stream.available() > 0) add(Color(stream.read(), stream.read(), stream.read()))
				}
			)
		}
		addParser("acTL") { stream, chunk ->
			PNGAnimationControlChunk(
				stream.read32(),
				stream.read32()
			)
		}
		addParser("fcTL") { stream, chunk ->
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
				PNGDisposeOperation.mapping.getValue(stream.read()),
				PNGBlendOperation.mapping.getValue(stream.read())
			)
		}
		addParser("fdAT") { stream, chunk ->
			PNGFrameDataChunk(
				stream.read32(),
				stream.readAllBytes()
			)
		}
	}


	override var next: PNGChunk? = refineNext()
}