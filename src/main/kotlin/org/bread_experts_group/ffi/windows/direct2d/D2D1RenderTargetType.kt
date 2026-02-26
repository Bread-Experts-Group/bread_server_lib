package org.bread_experts_group.ffi.windows.direct2d

import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.generic.Mappable

enum class D2D1RenderTargetType(
	override val id: Int
) : Mappable<D2D1RenderTargetType, Int> {
	D2D1_RENDER_TARGET_TYPE_DEFAULT(0),
	D2D1_RENDER_TARGET_TYPE_SOFTWARE(1),
	D2D1_RENDER_TARGET_TYPE_HARDWARE(2);

	override val tag: String = name
}

val D2D1_RENDER_TARGET_TYPE = DWORD