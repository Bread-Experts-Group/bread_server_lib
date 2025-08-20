package org.bread_experts_group.coder.format.decode.png

import org.bread_experts_group.channel.SplitReadableChannel
import org.bread_experts_group.coder.format.decode.Decoder
import org.bread_experts_group.coder.format.decode.TimedBufferedImage
import org.bread_experts_group.coder.format.parse.png.PNGBlendOperation
import org.bread_experts_group.coder.format.parse.png.PNGDisposeOperation
import org.bread_experts_group.coder.format.parse.png.PNGHeaderFlags
import org.bread_experts_group.coder.format.parse.png.PNGInterlaceType
import org.bread_experts_group.coder.format.parse.png.chunk.PNGHeaderChunk
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.ByteLookupTable
import java.awt.image.LookupOp
import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel
import java.util.zip.Inflater
import kotlin.math.pow
import kotlin.time.Duration

class PNGImageDecoder(
	private val from: PNGHeaderChunk,
	palette: List<Color> = listOf(),
	paletteTransparency: List<Float> = listOf(),
	gamma: Double = 1.0,
	private val background: Color? = null
) : Decoder<TimedBufferedImage> {
	private var x: Int = 0
	private var y: Int = 0
	private var width: Int = 0
	private var height: Int = 0
	private var dispose: PNGDisposeOperation = PNGDisposeOperation.APNG_DISPOSE_OP_NONE
	private var blend: PNGBlendOperation = PNGBlendOperation.APNG_BLEND_OP_OVER
	private var delay: Duration = Duration.INFINITE
	private var data = SplitReadableChannel(emptyList())
	private val inflater = Inflater()
	private val lineDecoder = PNGLineDecoder(from, palette, paletteTransparency)
	val gammaLut = ByteArray(256) { i ->
		val corrected = 255.0 * (i / 255.0).pow(1.0 / gamma)
		corrected.toInt().coerceIn(0, 255).toByte()
	}.let { ByteLookupTable(0, arrayOf(it, it, it)) }

	private val channels = if (from.flags.contains(PNGHeaderFlags.PALETTE)) 1
	else ((if (from.flags.contains(PNGHeaderFlags.TRUE_COLOR)) 3 else 1) +
			if (from.flags.contains(PNGHeaderFlags.ALPHA)) 1 else 0)

	private fun BufferedImage.copy(): BufferedImage {
		return BufferedImage(
			this.colorModel,
			this.copyData(null),
			this.isAlphaPremultiplied,
			null
		)
	}

	private var canvas: BufferedImage = BufferedImage(
		from.width, from.height,
		BufferedImage.TYPE_INT_ARGB
	).also {
		it.createGraphics().apply {
			color = this@PNGImageDecoder.background
			fillRect(0, 0, it.width, it.height)
			dispose()
		}
	}

	private val inflaterBuffer = ByteBuffer.allocateDirect(8192)
	fun inflate(into: ByteArray) {
		var inflated = 0
		while (inflated < into.size) {
			val new = inflater.inflate(into, inflated, into.size - inflated)
			if (new == 0) {
				inflaterBuffer.clear()
				data.read(inflaterBuffer)
				inflaterBuffer.flip()
				inflater.setInput(inflaterBuffer)
				continue
			}
			inflated += new
		}
	}

	private val filter = ByteArray(1)
	private var readInto = ByteArray(0)
	override fun next(): TimedBufferedImage {
		val strideSize = when (from.interlaceType) {
			PNGInterlaceType.NONE -> ((width * from.bitDepth * channels) + 7) / 8
			else -> throw UnsupportedOperationException(from.interlaceType.toString())
		}
		if (readInto.size != strideSize) readInto = ByteArray(strideSize)
		lateinit var precopy: BufferedImage
		if (dispose == PNGDisposeOperation.APNG_DISPOSE_OP_PREVIOUS) precopy = canvas.copy()
		val blendImg = BufferedImage(
			width, height,
			BufferedImage.TYPE_INT_ARGB
		)
		val line = IntArray(width)
		lineDecoder.reset()
		for (lY in 0 until height) {
			inflate(filter)
			inflate(readInto)
			lineDecoder.setInput(filter[0].toInt() and 0xFF, readInto)
			lineDecoder.next(line)
			blendImg.setRGB(
				0, lY,
				width, 1,
				line, 0, 0
			)
		}
		LookupOp(gammaLut, null).filter(blendImg, blendImg)
		canvas.createGraphics().apply {
			composite = when (blend) {
				PNGBlendOperation.APNG_BLEND_OP_SOURCE -> AlphaComposite.Src
				PNGBlendOperation.APNG_BLEND_OP_OVER -> AlphaComposite.SrcOver
			}
			drawImage(blendImg, x, y, null)
			dispose()
		}
		val final = canvas.copy()
		when (dispose) {
			PNGDisposeOperation.APNG_DISPOSE_OP_NONE -> {}
			PNGDisposeOperation.APNG_DISPOSE_OP_BACKGROUND -> {
				val zeroRow = IntArray(width)
				for (yy in y until y + height) {
					canvas.setRGB(
						x, yy,
						width, 1,
						zeroRow, 0, 0
					)
				}
			}

			PNGDisposeOperation.APNG_DISPOSE_OP_PREVIOUS -> {
				canvas = precopy
			}
		}
		return TimedBufferedImage(final, delay)
	}

	fun setInput(
		data: Collection<ReadableByteChannel>,
		x: Int = 0, y: Int = 0,
		width: Int = from.width, height: Int = from.height,
		dispose: PNGDisposeOperation = PNGDisposeOperation.APNG_DISPOSE_OP_NONE,
		blend: PNGBlendOperation = this.blend,
		delay: Duration = Duration.INFINITE
	) {
		this.data = SplitReadableChannel(data)
		inflater.reset()
		this.x = x
		this.y = y
		this.width = width
		this.height = height
		this.dispose = dispose
		this.blend = blend
		this.delay = delay
	}
}