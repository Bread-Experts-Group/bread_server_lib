package org.bread_experts_group.api.graphics.feature.direct2d

import org.bread_experts_group.ffi.autoArena
import org.bread_experts_group.ffi.windows.direct2d.*
import java.lang.foreign.MemorySegment

class Direct2DRectangleFloat {
	internal val ptr: MemorySegment = autoArena.allocate(D2D1_RECT_F)

	var left: Float
		get() = D2D1_RECT_F_left.get(ptr, 0) as Float
		set(value) = D2D1_RECT_F_left.set(ptr, 0, value)
	var right: Float
		get() = D2D1_RECT_F_right.get(ptr, 0) as Float
		set(value) = D2D1_RECT_F_right.set(ptr, 0, value)
	var top: Float
		get() = D2D1_RECT_F_top.get(ptr, 0) as Float
		set(value) = D2D1_RECT_F_top.set(ptr, 0, value)
	var bottom: Float
		get() = D2D1_RECT_F_bottom.get(ptr, 0) as Float
		set(value) = D2D1_RECT_F_bottom.set(ptr, 0, value)
}