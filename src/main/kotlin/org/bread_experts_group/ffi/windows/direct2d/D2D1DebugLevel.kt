package org.bread_experts_group.ffi.windows.direct2d

import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.generic.Mappable

enum class D2D1DebugLevel(override val id: Int) : Mappable<D2D1FactoryType, Int> {
	D2D1_DEBUG_LEVEL_NONE(0),
	D2D1_DEBUG_LEVEL_ERROR(1),
	D2D1_DEBUG_LEVEL_WARNING(2),
	D2D1_DEBUG_LEVEL_INFORMATION(3);

	override val tag: String = name
}

val D2D1_DEBUG_LEVEL = DWORD