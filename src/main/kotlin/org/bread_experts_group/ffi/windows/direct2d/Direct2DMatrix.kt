package org.bread_experts_group.ffi.windows.direct2d

import org.bread_experts_group.ffi.globalArena
import java.lang.foreign.MemorySegment

val d2d1Matrix3x2FIdentity: MemorySegment = globalArena.allocate(D2D1_MATRIX_3X2_F).also {
	D2D_MATRIX_3X2_F__11.set(it, 0, 1f)
	D2D_MATRIX_3X2_F__22.set(it, 0, 1f)
}