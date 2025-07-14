package org.bread_experts_group.coder.format.parse.png

import org.bread_experts_group.coder.Mappable

enum class PNGPixelDimensions(
	override val id: Int,
	override val tag: String
) : Mappable<PNGPixelDimensions, Int> {
	UNKNOWN(0, "?"),
	METER(1, "m");

	override fun other(): PNGPixelDimensions? = UNKNOWN
	override fun toString(): String = stringForm()
}