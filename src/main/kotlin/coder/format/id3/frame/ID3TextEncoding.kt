package org.bread_experts_group.coder.format.id3.frame

import org.bread_experts_group.coder.Mappable
import java.nio.charset.Charset

enum class ID3TextEncoding(
	override val id: Int, override val tag: String,
	val charset: Charset
) : Mappable<ID3TextEncoding, Int> {
	ISO_8859_1(0, "ISO-8859-1", Charsets.ISO_8859_1),
	UTF_16(1, "UTF-16", Charsets.UTF_16);

	override fun toString(): String = stringForm()
}