package org.bread_experts_group.coder.format.parse.png

import org.bread_experts_group.coder.Mappable

enum class PNGInterlaceType(
	override val id: Int,
	override val tag: String
) : Mappable<PNGInterlaceType, Int> {
	NONE(0, "No Interlace"),
	ADAM7(1, "Adam-7 Interlace"),
	OTHER(-1, "Unknown");

	override fun other(): PNGInterlaceType? = OTHER
	override fun toString(): String = stringForm()
}