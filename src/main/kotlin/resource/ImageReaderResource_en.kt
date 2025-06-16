package org.bread_experts_group.resource

import java.util.*

/**
 * @author <a href="https://github.com/ATPStorages">Miko Elbrecht (EN)</a>
 * Translator
 * @since 2.19.0
 */
@Suppress("unused", "ClassName")
class ImageReaderResource_en : ListResourceBundle() {
	override fun getContents(): Array<out Array<out Any>> = arrayOf(
		arrayOf("apng_reader_spi_description", "Animated PNG (Portable Network Graphics) Reader Provider"),
		arrayOf("gif_reader_spi_description", "GIF (Graphics Interchange Format) Reader Provider"),
	)
}