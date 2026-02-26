package org.bread_experts_group.api.graphics.feature.direct2d

import org.bread_experts_group.ffi.autoArena
import org.bread_experts_group.ffi.windows.direct2d.D2D1_POINT_2F
import org.bread_experts_group.ffi.windows.direct2d.D2D1_POINT_2F_x
import org.bread_experts_group.ffi.windows.direct2d.D2D1_POINT_2F_y
import java.lang.foreign.MemorySegment

class Direct2DPoint2Float {
	internal val ptr: MemorySegment = autoArena.allocate(D2D1_POINT_2F)

	var x: Float
		get() = D2D1_POINT_2F_x.get(ptr, 0) as Float
		set(value) = D2D1_POINT_2F_x.set(ptr, 0, value)
	var y: Float
		get() = D2D1_POINT_2F_y.get(ptr, 0) as Float
		set(value) = D2D1_POINT_2F_y.set(ptr, 0, value)
}