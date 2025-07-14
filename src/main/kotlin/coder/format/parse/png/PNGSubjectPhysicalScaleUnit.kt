package org.bread_experts_group.coder.format.parse.png

import org.bread_experts_group.coder.Mappable

enum class PNGSubjectPhysicalScaleUnit(
	override val id: Int,
	override val tag: String
) : Mappable<PNGSubjectPhysicalScaleUnit, Int> {
	METER(1, "m"),
	RADIAN(2, "rad")
}