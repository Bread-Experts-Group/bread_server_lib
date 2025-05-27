package org.bread_experts_group.coder.format.iso_bmff.box

class ISOBMFFCopyrightBox(
	val language: Int,
	val notice: String
) : ISOBMFFBox("cprt", byteArrayOf()) {
	override fun toString(): String = "ISOBMFFBox.\"$name\"[$language, \"$notice\"]"
}