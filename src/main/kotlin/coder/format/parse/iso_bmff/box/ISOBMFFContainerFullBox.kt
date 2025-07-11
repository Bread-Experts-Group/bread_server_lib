package org.bread_experts_group.coder.format.parse.iso_bmff.box

import org.bread_experts_group.coder.format.parse.iso_bmff.ISOBMFFParser

class ISOBMFFContainerFullBox(
	tag: String,
	boxes: ISOBMFFParser,
	override val version: Int,
	override val flags: Int,
) : ISOBMFFContainerBox(tag, boxes), ISOBMFFFullBox {
	override fun toString(): String = super.toString() + fullBoxString()
}