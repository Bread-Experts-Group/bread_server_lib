package org.bread_experts_group.ffi.windows.direct2d

import org.bread_experts_group.ffi.globalArena
import java.lang.foreign.MemorySegment

val D2D1_CORNFLOWER_BLUE: MemorySegment = globalArena.allocate(D2D1_COLOR_F).also {
	D3DCOLORVALUE_r.set(it, 0, 0x64 / 255.0f)
	D3DCOLORVALUE_g.set(it, 0, 0x95 / 255.0f)
	D3DCOLORVALUE_b.set(it, 0, 0xED / 255.0f)
	D3DCOLORVALUE_a.set(it, 0, 1f)
}

val D2D1_DARK_BLUE: MemorySegment = globalArena.allocate(D2D1_COLOR_F).also {
	D3DCOLORVALUE_b.set(it, 0, 0x8B / 255.0f)
	D3DCOLORVALUE_a.set(it, 0, 1f)
}

val D2D1_LIGHT_SLATE_GRAY: MemorySegment = globalArena.allocate(D2D1_COLOR_F).also {
	D3DCOLORVALUE_r.set(it, 0, 0x77 / 255.0f)
	D3DCOLORVALUE_g.set(it, 0, 0x88 / 255.0f)
	D3DCOLORVALUE_b.set(it, 0, 0x99 / 255.0f)
	D3DCOLORVALUE_a.set(it, 0, 1f)
}

val D2D1_BLACK: MemorySegment = globalArena.allocate(D2D1_COLOR_F).also {
	D3DCOLORVALUE_a.set(it, 0, 1f)
}

val D2D1_WHITE: MemorySegment = globalArena.allocate(D2D1_COLOR_F).also {
	D3DCOLORVALUE_r.set(it, 0, 0xFF / 255.0f)
	D3DCOLORVALUE_g.set(it, 0, 0xFF / 255.0f)
	D3DCOLORVALUE_b.set(it, 0, 0xFF / 255.0f)
	D3DCOLORVALUE_a.set(it, 0, 1f)
}