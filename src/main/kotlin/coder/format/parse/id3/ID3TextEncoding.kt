package org.bread_experts_group.coder.format.parse.id3

import org.bread_experts_group.coder.Mappable
import java.nio.charset.Charset

enum class ID3TextEncoding(
	override val id: Int, override val tag: String,
	val charset: Charset
) : Mappable<ID3TextEncoding, Int> {
	ISO_8859_1(0, "ISO-8859-1", Charsets.ISO_8859_1),
	UTF_16(1, "UTF-16", Charsets.UTF_16),
	UTF_16_BE(2, " UTF-16 Big Endian", Charsets.UTF_16BE),
	UTF_8(3, "UTF-8", Charsets.UTF_8);

	override fun toString(): String = stringForm()
}