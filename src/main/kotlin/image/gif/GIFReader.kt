package org.bread_experts_group.image.gif

import org.bread_experts_group.coder.format.parse.gif.GIFDisposalMethod
import org.bread_experts_group.coder.format.parse.gif.GIFParser
import org.bread_experts_group.coder.format.parse.gif.block.GIFGraphicControlExtensionBlock
import org.bread_experts_group.coder.format.parse.gif.block.GIFImageDescriptor
import org.bread_experts_group.coder.format.parse.gif.block.GIFLogicalScreenDescriptorBlock
import org.bread_experts_group.image.AnimatedMetadata
import org.bread_experts_group.stream.DataInputProxyStream
import java.awt.AlphaComposite
import java.awt.image.BufferedImage
import java.io.InputStream
import javax.imageio.IIOImage
import javax.imageio.ImageReadParam
import javax.imageio.ImageReader
import javax.imageio.ImageTypeSpecifier
import javax.imageio.metadata.IIOMetadata
import javax.imageio.stream.ImageInputStream

class GIFReader(spi: GIFReaderSpi) : ImageReader(spi) {
	private var readImages = emptyArray<IIOImage>()
	private var initialized = false

	@OptIn(ExperimentalStdlibApi::class)
	private fun readAll() {
		if (initialized) return
		initialized = true
		val parsed = GIFParser()
			.setInput(
				when (this.input) {
					is ImageInputStream -> DataInputProxyStream(this.input as ImageInputStream)
					is InputStream -> this.input as InputStream
					else -> throw UnsupportedOperationException(this.input::class.java.canonicalName)
				}
			)
			.toList()
		var (canvas, globalColors) = (parsed.first() as GIFLogicalScreenDescriptorBlock).run {
			Triple(
				BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB),
				colors, backgroundColor
			)
		}
		val newReadImages = mutableListOf<IIOImage>()
		parsed.forEachIndexed { i, block ->
			try {
				if (block is GIFImageDescriptor) {
					val control = parsed[i - 1] as? GIFGraphicControlExtensionBlock
					val colors = block.localColors ?: globalColors
					if (colors == null) throw IllegalStateException("A color table must be present")
					val rgb = IntArray(block.width * block.height)
					val reader = GIFLZWDecoder(block.rasterData, block.lzwMinimumCodeSize)
					reader.decompress().forEachIndexed { i, index ->
						if (index != control?.transparentColorIndex) rgb[i] = colors[index].rgb
					}
					val newImage = BufferedImage(block.width, block.height, BufferedImage.TYPE_INT_ARGB)
					newImage.setRGB(
						0, 0,
						block.width, block.height,
						rgb, 0, block.width
					)

					val precopy = if (control?.dispose == GIFDisposalMethod.RESTORE_TO_PREVIOUS)
						BufferedImage(canvas.colorModel, canvas.copyData(null), canvas.isAlphaPremultiplied, null)
					else null
					val graphics = canvas.createGraphics()
					graphics.composite = AlphaComposite.SrcOver
					graphics.drawImage(newImage, block.x, block.y, null)
					val copy = BufferedImage(
						canvas.colorModel,
						canvas.copyData(null),
						canvas.isAlphaPremultiplied,
						null
					)
					when (control?.dispose) {
						GIFDisposalMethod.UNSPECIFIED, GIFDisposalMethod.DO_NOT_DISPOSE, null -> {}
						GIFDisposalMethod.RESTORE_TO_BACKGROUND -> {
							graphics.fillRect(
								block.x, block.y,
								block.width, block.height
							)
						}

						GIFDisposalMethod.RESTORE_TO_PREVIOUS -> canvas = precopy!!
					}
					graphics.dispose()

					newReadImages.add(
						IIOImage(
							copy,
							null,
							if (control != null) AnimatedMetadata(control.delay) else null
						)
					)
				}
			} catch (e: Throwable) {
				e.printStackTrace()
			}
		}
		readImages = newReadImages.toTypedArray()
	}

	override fun getNumImages(allowSearch: Boolean): Int = readImages.size
	override fun getWidth(imageIndex: Int): Int = readImages[imageIndex].renderedImage.width
	override fun getHeight(imageIndex: Int): Int = readImages[imageIndex].renderedImage.height

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
		return readImages[imageIndex].renderedImage as BufferedImage
	}
}