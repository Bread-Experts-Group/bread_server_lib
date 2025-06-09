package org.bread_experts_group.coder.format.iso_bmff.box

import org.bread_experts_group.stream.write16
import org.bread_experts_group.stream.writeString
import java.io.OutputStream

class ISOBMFFCopyrightBox(
	val language: Int,
	val notice: String
) : ISOBMFFBox("cprt", byteArrayOf()) {
	override fun toString(): String = "ISOBMFFBox.\"$tag\"[$language, \"$notice\"]"
	override fun computeSize(): Long = 2L + notice.length
	override fun write(stream: OutputStream) {
		super.write(stream)
		stream.write16(language)
		stream.writeString(notice)
	}
}