package org.bread_experts_group.coder.format.parse.iso_bmff.box

import org.bread_experts_group.coder.format.parse.Parser
import java.io.InputStream

class ISOBMFFContainerFullBox(
	tag: String,
	boxes: Parser<String, ISOBMFFBox, InputStream>,
	override val version: Int,
	override val flags: Int,
) : ISOBMFFContainerBox(tag, boxes), ISOBMFFFullBox {
	override fun toString(): String = super.toString() + fullBoxString()
}