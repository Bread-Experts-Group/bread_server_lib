package org.bread_experts_group.coder.format.parse.png

import org.bread_experts_group.coder.Mappable

enum class PNGPrintOffsetUnit(
	override val id: Int,
	override val tag: String
) : Mappable<PNGPrintOffsetUnit, Int> {
	PIXEL(0, "px"),
	MICROMETER(1, "µm");

	override fun toString(): String = stringForm()
}