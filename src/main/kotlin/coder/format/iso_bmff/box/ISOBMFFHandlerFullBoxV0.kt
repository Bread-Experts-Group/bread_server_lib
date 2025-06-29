package org.bread_experts_group.coder.format.iso_bmff.box

class ISOBMFFHandlerFullBoxV0(
	override val flags: Int,
	val padding: Int,
	val handlerType: String,
	val reserved: IntArray,
	val name: String
) : ISOBMFFBox("hdlr", byteArrayOf()), ISOBMFFFullBox {
	override val version: Int = 0
	override fun toString(): String = "ISOBMFFBox.\"$tag\"[type: \"$handlerType\", name: \"$name\"]" + fullBoxString()
}