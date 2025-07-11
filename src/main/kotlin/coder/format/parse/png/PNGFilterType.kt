package org.bread_experts_group.coder.format.parse.png

import org.bread_experts_group.coder.Mappable

enum class PNGFilterType(
	override val id: Int,
	override val tag: String
) : Mappable<PNGFilterType, Int> {
	ADAPTIVE(0, "Adaptive Filtering"),
	OTHER(-1, "Unknown");

	override fun other(): PNGFilterType? = OTHER
	override fun toString(): String = stringForm()
}