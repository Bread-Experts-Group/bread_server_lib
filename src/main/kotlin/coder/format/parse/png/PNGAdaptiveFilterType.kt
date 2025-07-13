package org.bread_experts_group.coder.format.parse.png

import org.bread_experts_group.coder.Mappable

enum class PNGAdaptiveFilterType(
	override val id: Int,
	override val tag: String
) : Mappable<PNGAdaptiveFilterType, Int> {
	NONE(0, "No Filtering"),
	SUBTRACT(1, "Subtraction Filtering (x-1)"),
	UP(2, "Subtraction Filtering (y-1)"),
	AVERAGE(3, "Average Filtering"),
	PAETH(4, "Paeth-prediction Filtering");

	override fun toString(): String = stringForm()
}