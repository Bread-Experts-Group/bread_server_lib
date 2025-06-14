package org.bread_experts_group.taggart.apng

import java.util.*
import javax.imageio.ImageReader
import javax.imageio.spi.ImageReaderSpi
import javax.imageio.stream.ImageInputStream

class APNGReaderSpi : ImageReaderSpi() {
	init {
		vendorName = "Bread Experts Group"
		version = "1.0.0"
		names = arrayOf("coder/format/png", "apng")
		suffixes = arrayOf("coder/format/png", "apng")
		MIMETypes = arrayOf("image/png", "image/apng")
		pluginClassName = APNGReader::class.java.name
		inputTypes = arrayOf(ImageInputStream::class.java)
		supportsStandardStreamMetadataFormat = false
		supportsStandardImageMetadataFormat = false
	}

	override fun canDecodeInput(source: Any?): Boolean {
		TODO("Not yet implemented")
	}

	override fun createReaderInstance(extension: Any?): ImageReader = APNGReader(this)

	override fun getDescription(locale: Locale?): String {
		TODO("Not yet implemented")
	}
}