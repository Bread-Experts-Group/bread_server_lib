package org.bread_experts_group.coder.format.parse.png

import org.bread_experts_group.coder.Mappable

enum class PNGStereoscopyMode(
	override val id: Int,
	override val tag: String
) : Mappable<PNGStereoscopyMode, Int> {
	CROSS_FUSE_LAYOUT(0, "Cross-fuse Layout"),
	DIVERGING_FUSE_LAYOUT(1, "Diverging-fuse Layout");

	override fun toString(): String = stringForm()
}