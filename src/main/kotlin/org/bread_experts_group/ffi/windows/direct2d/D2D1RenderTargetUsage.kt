package org.bread_experts_group.ffi.windows.direct2d

import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.generic.Mappable

enum class D2D1RenderTargetUsage(override val id: Int) : Mappable<D2D1RenderTargetUsage, Int> {
	D2D1_RENDER_TARGET_USAGE_NONE(0x00000000),
	D2D1_RENDER_TARGET_USAGE_FORCE_BITMAP_REMOTING(0x00000001),
	D2D1_RENDER_TARGET_USAGE_GDI_COMPATIBLE(0x00000002);

	override val tag: String = name
}

val D2D1_RENDER_TARGET_USAGE = DWORD