package org.bread_experts_group.ffi.windows.directwrite

import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.generic.Mappable

enum class DWriteFontStretch : Mappable<DWriteFontWeight, Int> {
	DWRITE_FONT_STRETCH_UNDEFINED,
	DWRITE_FONT_STRETCH_ULTRA_CONDENSED,
	DWRITE_FONT_STRETCH_EXTRA_CONDENSED,
	DWRITE_FONT_STRETCH_CONDENSED,
	DWRITE_FONT_STRETCH_SEMI_CONDENSED,
	DWRITE_FONT_STRETCH_NORMAL,
	DWRITE_FONT_STRETCH_MEDIUM,
	DWRITE_FONT_STRETCH_SEMI_EXPANDED,
	DWRITE_FONT_STRETCH_EXPANDED,
	DWRITE_FONT_STRETCH_EXTRA_EXPANDED,
	DWRITE_FONT_STRETCH_ULTRA_EXPANDED;

	override val tag: String = name
	override val id: Int = ordinal
	override fun toString(): String = stringForm()
}

val DWRITE_FONT_STRETCH = DWORD