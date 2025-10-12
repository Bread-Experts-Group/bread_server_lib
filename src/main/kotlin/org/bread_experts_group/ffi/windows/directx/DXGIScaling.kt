package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.Mappable
import java.lang.foreign.ValueLayout

enum class DXGIScaling(
	override val id: UInt
) : Mappable<DXGIScaling, UInt> {
	DXGI_SCALING_STRETCH(0u),
	DXGI_SCALING_NONE(1u),
	DXGI_SCALING_ASPECT_RATIO_STRETCH(2u);

	override val tag: String = name
	override fun toString(): String = stringForm()
}

val DXGI_SCALING: ValueLayout.OfInt = ValueLayout.JAVA_INT