package org.bread_experts_group.resource

import java.util.*

/**
 * @author <a href="https://github.com/ATPStorages">Miko Elbrecht (EN)</a>
 * Translator
 * @since 2.41.0
 */
@Suppress("unused", "ClassName")
class ImageReaderResource_ja : ListResourceBundle() {
	override fun getContents(): Array<out Array<out Any>> = arrayOf(
		arrayOf("apng_reader_spi_description", "アニメーションPNG（APNG）読み取りSPI"),
		arrayOf("gif_reader_spi_description", "グラフィック交換様式（GIF）読み取りSPI"),
	)
}