package org.bread_experts_group.image.gif

import org.bread_experts_group.resource.ImageReaderResource
import java.io.InputStream
import java.util.*
import javax.imageio.ImageReader
import javax.imageio.spi.ImageReaderSpi
import javax.imageio.stream.ImageInputStream

class GIFReaderSpi : ImageReaderSpi() {
	init {
		vendorName = "Bread Experts Group"
		version = "1.0.0"
		names = arrayOf("gif")
		suffixes = arrayOf("gif")
		MIMETypes = arrayOf("image/gif")
		pluginClassName = GIFReader::class.java.name
		inputTypes = arrayOf(ImageInputStream::class.java, InputStream::class.java)
		supportsStandardStreamMetadataFormat = false
		supportsStandardImageMetadataFormat = false
	}

	override fun canDecodeInput(source: Any?): Boolean {
		if (source !is ImageInputStream) return false
		if (source !is InputStream) return false
		return true
	}

	override fun createReaderInstance(extension: Any?): ImageReader = GIFReader(this)

	override fun getDescription(locale: Locale?): String = ImageReaderResource.get()
		.getString("gif_reader_spi_description")
}