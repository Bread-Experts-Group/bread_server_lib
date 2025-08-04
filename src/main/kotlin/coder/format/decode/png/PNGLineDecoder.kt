package org.bread_experts_group.coder.format.decode.png

import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.coder.format.parse.png.PNGAdaptiveFilterType
import org.bread_experts_group.coder.format.parse.png.PNGHeaderFlags
import org.bread_experts_group.coder.format.parse.png.chunk.PNGHeaderChunk
import org.bread_experts_group.stream.maskI
import java.awt.Color
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

class PNGLineDecoder(
	private val from: PNGHeaderChunk,
	private val palette: List<Color> = listOf(),
	private val paletteTransparency: List<Float> = listOf()
) {
	private var filter: PNGAdaptiveFilterType = PNGAdaptiveFilterType.NONE
	private var data: ByteArray = byteArrayOf()
	private var lastData: ByteArray = byteArrayOf()
	private val channels =
		(if (from.flags.contains(PNGHeaderFlags.PALETTE)) max(1, from.bitDepth / 8)
		else ((if (from.flags.contains(PNGHeaderFlags.TRUE_COLOR)) 3 else 1) +
				if (from.flags.contains(PNGHeaderFlags.ALPHA)) 1 else 0)) * max(1, from.bitDepth / 8)

	private fun paethPredictor(a: Int, b: Int, c: Int): Int {
		val p = a + b - c
		val pa = abs(p - a)
		val pb = abs(p - b)
		val pc = abs(p - c)
		return when (minOf(pa, pb, pc)) {
			pa -> a
			pb -> b
			else -> c
		}
	}

	fun next(into: IntArray): IntArray {
		for (i in data.indices) {
			data[i] = ((data[i].toInt() and 0xFF) + when (filter) {
				PNGAdaptiveFilterType.NONE -> 0
				PNGAdaptiveFilterType.SUBTRACT -> data.getOrElse(i - channels) { 0 }.toInt() and 0xFF
				PNGAdaptiveFilterType.UP -> lastData.getOrElse(i) { 0 }.toInt() and 0xFF
				PNGAdaptiveFilterType.AVERAGE -> {
					val left = data.getOrElse(i - channels) { 0 }.toInt() and 0xFF
					val top = lastData.getOrElse(i) { 0 }.toInt() and 0xFF
					(left + top) / 2
				}

				PNGAdaptiveFilterType.PAETH -> run {
					val a = (data.getOrElse(i - channels) { 0 }).toInt() and 0xFF
					val b = (lastData.getOrElse(i) { 0 }).toInt() and 0xFF
					val c = (lastData.getOrElse(i - channels) { 0 }).toInt() and 0xFF
					paethPredictor(a, b, c)
				}
			}).toByte()
		}
		if (lastData.isEmpty()) lastData = ByteArray(data.size)
		System.arraycopy(data, 0, lastData, 0, data.size)
		var samplesOffset = 0
		val nextByte = { data[samplesOffset++].toInt() and 0xFF }
		val next = when (from.bitDepth) {
			1, 2, 4, 8 -> nextByte
			16 -> {
				{
					((nextByte() shl 8) or nextByte()) ushr 8
				}
			}

			else -> throw UnsupportedOperationException("True-color ${from.bitDepth}-bit")
		}
		var i = 0
		while (i < into.size) {
			if (from.flags.contains(PNGHeaderFlags.PALETTE)) {
				when (from.bitDepth) {
					1, 2, 4 -> {
						val source = next()
						for (n in (0..7 step from.bitDepth).reversed()) {
							val local = (source shr n) and from.bitDepth.maskI()
							val trans = paletteTransparency.getOrNull(local)?.times(0xFF)?.roundToInt() ?: 0xFF
							into[i++] = (palette[local].rgb and 0xFFFFFF) or (trans shl 24)
						}
					}

					8 -> {
						val local = next()
						val trans = paletteTransparency.getOrNull(local)?.times(0xFF)?.roundToInt() ?: 0xFF
						into[i++] = (palette[local].rgb and 0xFFFFFF) or (trans shl 24)
					}

					else -> throw UnsupportedOperationException("Palette ${from.bitDepth}-bit")
				}
			} else if (from.flags.contains(PNGHeaderFlags.TRUE_COLOR)) {
				val r = next()
				val g = next()
				val b = next()
				val a = if (from.flags.contains(PNGHeaderFlags.ALPHA)) next() else 0xFF
				into[i++] = (a shl 24) or (r shl 16) or (g shl 8) or b
			} else when (val bits = from.bitDepth) {
				1, 2, 4 -> {
					val source = next()
					for (n in (0..7 step bits).reversed()) {
						val s = ((source shr n) and ((1 shl bits) - 1))
						val white = when (bits) {
							1 -> (-s ushr 24)
							2 -> (s shl 6) or (s shl 4) or (s shl 2) or s
							4 -> (s shl 4) or s
							else -> 0
						}
						into[i++] = (0xFF shl 24) or (white shl 16) or (white shl 8) or white
					}
				}

				8 -> {
					val white = next()
					val a = if (from.flags.contains(PNGHeaderFlags.ALPHA)) next() else 0xFF
					into[i++] = (a shl 24) or (white shl 16) or (white shl 8) or white
				}

				16 -> {
					val white = next()
					val a = if (from.flags.contains(PNGHeaderFlags.ALPHA)) next() else 0xFF
					into[i++] = (a shl 24) or (white shl 16) or (white shl 8) or white
				}

				else -> throw NotImplementedError("Grayscale ${from.bitDepth}-bit")
			}
		}
		return into
	}

	fun setInput(filter: Int, data: ByteArray) {
		this.filter = PNGAdaptiveFilterType.entries.id(filter).enum
		this.data = data
	}

	fun reset() {
		this.lastData = byteArrayOf()
	}
}