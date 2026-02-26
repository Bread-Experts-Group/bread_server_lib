package org.bread_experts_group.ffi.windows.direct2d

import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.generic.Mappable

enum class D2D1PresentOptions(override val id: Int) : Mappable<D2D1FactoryType, Int> {
	D2D1_PRESENT_OPTIONS_NONE(0x00000000),
	D2D1_PRESENT_OPTIONS_RETAIN_CONTENTS(0x00000001),
	D2D1_PRESENT_OPTIONS_IMMEDIATELY(0x00000002);

	override val tag: String = name
}

val D2D1_PRESENT_OPTIONS = DWORD