package org.bread_experts_group.image.apng

import org.bread_experts_group.coder.format.png.PNGAdaptiveFilterType
import org.bread_experts_group.coder.format.png.PNGBlendOperation
import org.bread_experts_group.coder.format.png.PNGDisposeOperation
import org.bread_experts_group.coder.format.png.PNGParser
import org.bread_experts_group.coder.format.png.chunk.*
import org.bread_experts_group.stream.DataInputProxyStream
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.DataBuffer
import java.awt.image.IndexColorModel
import java.io.InputStream
import java.nio.ByteBuffer
import java.util.zip.InflaterInputStream
import javax.imageio.IIOImage
import javax.imageio.ImageReadParam
import javax.imageio.ImageReader
import javax.imageio.ImageTypeSpecifier
import javax.imageio.metadata.IIOMetadata
import javax.imageio.stream.ImageInputStream
import kotlin.math.abs

class APNGReader(spi: APNGReaderSpi) : ImageReader(spi) {
	private val bufferedImages = mutableListOf<BufferedImage>()
	private val readImages = mutableListOf<IIOImage>()
	private fun paethPredictor(a: Int, b: Int, c: Int): Int {
		val p = a + b - c
		val pa = abs(p - a)
		val pb = abs(p - b)
		val pc = abs(p - c)
		return when {
			pa <= pb && pa <= pc -> a
			pb <= pc -> b
			else -> c
		}
	}

	private fun imageTypeForHeader(header: PNGHeaderChunk): Int = when {
		header.bitDepth < 8 -> BufferedImage.TYPE_BYTE_BINARY
		header.palette -> BufferedImage.TYPE_BYTE_INDEXED
		header.color && header.alpha -> BufferedImage.TYPE_INT_ARGB
		header.color -> BufferedImage.TYPE_INT_RGB
		header.bitDepth == 8 && !header.color -> BufferedImage.TYPE_BYTE_GRAY
		else -> throw IllegalArgumentException("Unsupported set $header")
	}

	private fun decodeImage(
		header: PNGHeaderChunk,
		width: Int, height: Int,
		data: InputStream, palette: List<Color>?
	): BufferedImage {
		val imageType = imageTypeForHeader(header)
		val bufferedImage = when (imageType) {
			BufferedImage.TYPE_INT_RGB, BufferedImage.TYPE_INT_ARGB,
			BufferedImage.TYPE_BYTE_GRAY -> BufferedImage(width, height, imageType)

			BufferedImage.TYPE_BYTE_BINARY, BufferedImage.TYPE_BYTE_INDEXED -> BufferedImage(
				width, height, imageType,
				IndexColorModel(
					header.bitDepth, palette!!.size, palette.map { it.rgb }.toIntArray(),
					0, false, -1, DataBuffer.TYPE_BYTE
				)
			)

			else -> TODO(imageType.toString())
		}
		val channels = bufferedImage.raster.sampleModel.numBands + when {
			imageType == BufferedImage.TYPE_BYTE_GRAY && header.alpha -> 1
			else -> 0
		}
		var lastStride = ByteArray((width * header.bitDepth * channels) / 8)
		val thisStride = ByteArray(lastStride.size)
		val pixel = ByteBuffer.allocate(4)
		for (y in 0 until height) {
			val filter = PNGAdaptiveFilterType.mapping.getValue(data.read())
			data.readNBytes(thisStride, 0, thisStride.size)
			thisStride.forEachIndexed { i, byte ->
				thisStride[i] = ((byte.toInt() and 0xFF) + when (filter) {
					PNGAdaptiveFilterType.NONE -> 0
					PNGAdaptiveFilterType.SUBTRACT -> thisStride.getOrElse(i - channels) { 0 }.toInt() and 0xFF
					PNGAdaptiveFilterType.UP -> lastStride.getOrElse(i) { 0 }.toInt() and 0xFF
					PNGAdaptiveFilterType.AVERAGE -> {
						val left = thisStride.getOrElse(i - channels) { 0 }.toInt() and 0xFF
						val top = lastStride.getOrElse(i) { 0 }.toInt() and 0xFF
						(left + top) / 2
					}

					PNGAdaptiveFilterType.PAETH -> run {
						val a = (thisStride.getOrElse(i - channels) { 0 }).toInt() and 0xFF
						val b = (lastStride.getOrElse(i) { 0 }).toInt() and 0xFF
						val c = (lastStride.getOrElse(i - channels) { 0 }).toInt() and 0xFF
						paethPredictor(a, b, c)
					}
				}).toByte()
			}
			lastStride = thisStride.copyOf()
			val rgb = IntArray(width)
			var skip = 0
			for (i in 0 until rgb.size) {
				if (skip > 0) {
					skip--
					continue
				}
				pixel.clear()
				pixel.put(thisStride, (i * header.bitDepth * channels) / 8, channels)
				pixel.flip()
				when (imageType) {
					BufferedImage.TYPE_BYTE_GRAY -> {
						val grayscale = pixel.get().toInt() and 0xFF
						rgb[i] = Color(
							grayscale, grayscale, grayscale,
							if (header.alpha) pixel.get().toInt() and 0xFF else 255
						).rgb
					}

					BufferedImage.TYPE_BYTE_BINARY, BufferedImage.TYPE_BYTE_INDEXED -> when (header.bitDepth) {
						1 -> {
							var index = i
							var remainder = pixel.get().toInt() and 0xFF
							repeat(8) {
								rgb[index++] = palette!![(remainder and 0b10000000) shr 7].rgb
								remainder = remainder shl 1
							}
							skip = 7
						}

						2 -> {
							var index = i
							var remainder = pixel.get().toInt() and 0xFF
							repeat(4) {
								rgb[index++] = palette!![(remainder and 0b11000000) shr 6].rgb
								remainder = remainder shl 2
							}
							skip = 3
						}

						4 -> {
							var index = i
							var remainder = pixel.get().toInt() and 0xFF
							repeat(2) {
								rgb[index++] = palette!![(remainder and 0b11110000) shr 4].rgb
								remainder = remainder shl 4
							}
							skip = 1
						}

						8 -> {
							rgb[i] = palette!![pixel.get().toInt() and 0xFF].rgb
						}

						else -> throw UnsupportedOperationException("Bit depth [$header]")
					}

					BufferedImage.TYPE_INT_RGB, BufferedImage.TYPE_INT_ARGB -> rgb[i] = Color(
						pixel.get().toInt() and 0xFF,
						pixel.get().toInt() and 0xFF,
						pixel.get().toInt() and 0xFF,
						if (imageType == BufferedImage.TYPE_INT_RGB) 255
						else pixel.get().toInt() and 0xFF,
					).rgb

					else -> throw IllegalArgumentException("Bad color type [$imageType]")
				}
			}
			bufferedImage.setRGB(
				0, y, width, 1,
				rgb, 0, rgb.size
			)
		}
		return bufferedImage
	}

	private fun readAll() {
		if (readImages.isNotEmpty()) return
		val parsed = PNGParser(
			when (this.input) {
				is ImageInputStream -> DataInputProxyStream(this.input as ImageInputStream)
				is InputStream -> this.input as InputStream
				else -> throw UnsupportedOperationException(this.input::class.java.canonicalName)
			}
		).readAllParsed().fold(mutableListOf<PNGChunk>()) { acc, chunk ->
			if ((chunk.tag == "IDAT" || chunk.tag == "fdAT") && acc.lastOrNull()?.tag == chunk.tag) {
				val last = acc.removeLast()
				acc += PNGChunk(chunk.tag, last.data + chunk.data)
			} else acc += chunk
			acc
		}
		val header = parsed.firstNotNullOf { it as? PNGHeaderChunk }
		val palette = parsed.firstNotNullOfOrNull { it as? PNGPaletteChunk }
		run {
			val merged = parsed.first { it.tag == "IDAT" }.data.inputStream()
			val data = InflaterInputStream(merged)
			val mainImage = decodeImage(header, header.width, header.height, data, palette?.colors)
			readImages.add(0, IIOImage(mainImage, listOf(), null))
			bufferedImages.add(mainImage)
		}
		run {
			var canvas = BufferedImage(header.width, header.height, imageTypeForHeader(header))
			val control = parsed.firstNotNullOfOrNull { it as? PNGAnimationControlChunk }
			if (control == null) return
			val ani = parsed.filter { it.tag == "IDAT" || it.tag == "fdAT" || it is PNGSequencedChunk }
				.chunked(2)
				.toMutableList()
			while (ani.isNotEmpty()) {
				val (control, data) = ani.removeFirst()
				control as PNGFrameControlChunk
				if (control.sequence == 0) {
					readImages[0].metadata = APNGMetadata(control.delayMillis)
				} else bufferedImages.add(run {
					val data = InflaterInputStream(data.data.inputStream())
					val newImage = decodeImage(
						header, control.width, control.height,
						data, palette?.colors
					)
					val precopy = if (control.dispose == PNGDisposeOperation.APNG_DISPOSE_OP_PREVIOUS)
						BufferedImage(canvas.colorModel, canvas.copyData(null), canvas.isAlphaPremultiplied, null)
					else null
					val graphics = canvas.createGraphics()
					graphics.composite =
						if (control.blend == PNGBlendOperation.APNG_BLEND_OP_OVER) AlphaComposite.SrcOver
						else AlphaComposite.Src
					graphics.drawImage(newImage, control.x, control.y, null)
					val copy =
						BufferedImage(canvas.colorModel, canvas.copyData(null), canvas.isAlphaPremultiplied, null)
					when (control.dispose) {
						PNGDisposeOperation.APNG_DISPOSE_OP_NONE -> {}
						PNGDisposeOperation.APNG_DISPOSE_OP_BACKGROUND -> {
							graphics.composite = AlphaComposite.Clear
							graphics.fillRect(
								control.x, control.y,
								control.width, control.height
							)
						}

						PNGDisposeOperation.APNG_DISPOSE_OP_PREVIOUS -> canvas = precopy!!
					}
					graphics.dispose()
					readImages.add(IIOImage(copy, listOf(), APNGMetadata(control.delayMillis)))
					copy
				})
			}
		}
	}

	override fun getNumImages(allowSearch: Boolean): Int = readImages.size
	override fun getWidth(imageIndex: Int): Int = bufferedImages[imageIndex].width
	override fun getHeight(imageIndex: Int): Int = bufferedImages[imageIndex].height

	override fun getImageTypes(imageIndex: Int): Iterator<ImageTypeSpecifier> {
		return arrayOf(ImageTypeSpecifier.createFromRenderedImage(readImages[imageIndex].renderedImage)).iterator()
	}

	override fun getStreamMetadata(): IIOMetadata? = null
	override fun getImageMetadata(imageIndex: Int): IIOMetadata? {
		readAll()
		return readImages[imageIndex].metadata
	}

	override fun read(imageIndex: Int, param: ImageReadParam?): BufferedImage {
		readAll()
		return bufferedImages[imageIndex]
	}
}