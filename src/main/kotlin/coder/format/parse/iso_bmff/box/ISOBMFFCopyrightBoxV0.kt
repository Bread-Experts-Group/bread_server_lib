package org.bread_experts_group.coder.format.parse.iso_bmff.box

import java.io.OutputStream
import java.util.*

class ISOBMFFCopyrightBoxV0(
	override val flags: Int,
	val language: Locale?,
	val notice: String
) : ISOBMFFBox("cprt", byteArrayOf()), ISOBMFFFullBox {
	override val version: Int = 0
	override fun toString(): String = "ISOBMFFBox.\"$tag\"[$language, \"$notice\"]" + fullBoxString()
	override fun computeSize(): Long = TODO("Cprt")// 2L + notice.length
	override fun write(stream: OutputStream) {
//		super.write(stream)
//		stream.write16(language)
//		stream.writeString(notice)
		TODO("Cprt")
	}
}