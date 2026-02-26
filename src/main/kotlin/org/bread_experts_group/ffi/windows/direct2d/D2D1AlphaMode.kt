package org.bread_experts_group.ffi.windows.direct2d

import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.generic.Mappable

enum class D2D1AlphaMode(override val id: Int) : Mappable<D2D1FactoryType, Int> {
	D2D1_ALPHA_MODE_UNKNOWN(0),
	D2D1_ALPHA_MODE_PREMULTIPLIED(1),
	D2D1_ALPHA_MODE_STRAIGHT(2),
	D2D1_ALPHA_MODE_IGNORE(3);

	override val tag: String = name
}

val D2D1_ALPHA_MODE = DWORD