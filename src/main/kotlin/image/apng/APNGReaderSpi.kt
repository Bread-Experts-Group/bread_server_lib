package org.bread_experts_group.image.apng

import java.io.InputStream
import java.util.*
import javax.imageio.ImageReader
import javax.imageio.spi.ImageReaderSpi
import javax.imageio.stream.ImageInputStream

class APNGReaderSpi : ImageReaderSpi() {
	init {
		vendorName = "Bread Experts Group"
		version = "1.0.0"
		names = arrayOf("png", "apng")
		suffixes = arrayOf("png", "apng")
		MIMETypes = arrayOf("image/png", "image/apng")
		pluginClassName = APNGReader::class.java.name
		inputTypes = arrayOf(ImageInputStream::class.java, InputStream::class.java)
		supportsStandardStreamMetadataFormat = false
		supportsStandardImageMetadataFormat = false
	}

	override fun canDecodeInput(source: Any?): Boolean {
		if (source !is ImageInputStream) return false
		if (source !is InputStream) return false
		return true
	}

	override fun createReaderInstance(extension: Any?): ImageReader = APNGReader(this)

	override fun getDescription(locale: Locale?): String {
		val baseName = "org.bread_experts_group.resource.ImageReaderResource"
		val bundle =
			if (locale != null) ResourceBundle.getBundle(baseName, locale)
			else ResourceBundle.getBundle(baseName)
		return bundle.getString("apng_reader_spi_description")
	}
}