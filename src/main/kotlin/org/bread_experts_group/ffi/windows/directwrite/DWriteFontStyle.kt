package org.bread_experts_group.ffi.windows.directwrite

import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.generic.Mappable

enum class DWriteFontStyle : Mappable<DWriteFontWeight, Int> {
	DWRITE_FONT_STYLE_NORMAL,
	DWRITE_FONT_STYLE_OBLIQUE,
	DWRITE_FONT_STYLE_ITALIC;

	override val tag: String = name
	override val id: Int = ordinal
	override fun toString(): String = stringForm()
}

val DWRITE_FONT_STYLE = DWORD