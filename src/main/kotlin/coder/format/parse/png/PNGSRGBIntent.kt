package org.bread_experts_group.coder.format.parse.png

import org.bread_experts_group.coder.Mappable

enum class PNGSRGBIntent(
	override val id: Int,
	override val tag: String
) : Mappable<PNGSRGBIntent, Int> {
	PERCEPTUAL(0, "Gamut adaption preferred"),
	RELATIVE_COLORMETRIC(1, "Color appearance preferred"),
	SATURATION(2, "Saturation preservation preferred"),
	ABSOLUTE_COLORMETRIC(3, "Colormetry preservation preferred");

	override fun toString(): String = stringForm()
}