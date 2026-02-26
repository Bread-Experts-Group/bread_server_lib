package org.bread_experts_group.ffi.windows.direct2d

import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.generic.FlagSet
import org.bread_experts_group.generic.FlagSetConvertible

enum class D2D1DrawTextOptions : FlagSetConvertible {
	D2D1_DRAW_TEXT_OPTIONS_NO_SNAP,
	D2D1_DRAW_TEXT_OPTIONS_CLIP,
	D2D1_DRAW_TEXT_OPTIONS_ENABLE_COLOR_FONT,
	D2D1_DRAW_TEXT_OPTIONS_DISABLE_COLOR_BITMAP_SNAPPING
}

val D2D1_DRAW_TEXT_OPTIONS_NONE = FlagSet(D2D1DrawTextOptions::class.java, 0)
val D2D1_DRAW_TEXT_OPTIONS = DWORD