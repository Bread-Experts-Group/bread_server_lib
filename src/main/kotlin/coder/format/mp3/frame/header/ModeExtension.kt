package org.bread_experts_group.coder.format.mp3.frame.header

import org.bread_experts_group.coder.Mappable

enum class ModeExtension(override var id: Int, override val tag: String) : Mappable<ModeExtension, Int> {
	NONE(0, "No Intensity / MS"),
	INTENSITY(1, "Intensity"),
	MS(2, "MS"),
	INTENSITY_MS(3, "Intensity / MS");

	override fun toString(): String = stringForm()
}