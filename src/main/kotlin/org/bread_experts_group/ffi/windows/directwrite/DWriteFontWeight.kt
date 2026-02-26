package org.bread_experts_group.ffi.windows.directwrite

import org.bread_experts_group.api.graphics.feature.directwrite.textformat.DirectWriteFontWeight
import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.generic.Mappable

enum class DWriteFontWeight(override val id: Int) : Mappable<DWriteFontWeight, Int>, DirectWriteFontWeight {
	DWRITE_FONT_WEIGHT_THIN(100),
	DWRITE_FONT_WEIGHT_EXTRA_LIGHT(200),
	DWRITE_FONT_WEIGHT_ULTRA_LIGHT(200),
	DWRITE_FONT_WEIGHT_LIGHT(300),
	DWRITE_FONT_WEIGHT_SEMI_LIGHT(350),
	DWRITE_FONT_WEIGHT_NORMAL(400),
	DWRITE_FONT_WEIGHT_REGULAR(400),
	DWRITE_FONT_WEIGHT_MEDIUM(500),
	DWRITE_FONT_WEIGHT_DEMI_BOLD(600),
	DWRITE_FONT_WEIGHT_SEMI_BOLD(600),
	DWRITE_FONT_WEIGHT_BOLD(700),
	DWRITE_FONT_WEIGHT_EXTRA_BOLD(800),
	DWRITE_FONT_WEIGHT_ULTRA_BOLD(800),
	DWRITE_FONT_WEIGHT_BLACK(900),
	DWRITE_FONT_WEIGHT_HEAVY(900),
	DWRITE_FONT_WEIGHT_EXTRA_BLACK(950),
	DWRITE_FONT_WEIGHT_ULTRA_BLACK(950);

	override val weight: Int = id
	override val tag: String = name
	override fun toString(): String = stringForm()
}

val DWRITE_FONT_WEIGHT = DWORD