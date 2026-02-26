package org.bread_experts_group.ffi.windows.directwrite

import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.generic.Mappable

enum class DWriteMeasuringMode(override val id: Int) : Mappable<DWriteMeasuringMode, Int> {
	DWRITE_MEASURING_MODE_NATURAL(0),
	DWRITE_MEASURING_MODE_GDI_CLASSIC(1),
	DWRITE_MEASURING_MODE_GDI_NATURAL(2);

	override val tag: String = name
	override fun toString(): String = stringForm()
}

val DWRITE_MEASURING_MODE = DWORD