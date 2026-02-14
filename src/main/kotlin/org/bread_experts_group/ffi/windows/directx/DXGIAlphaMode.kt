package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.generic.Mappable
import java.lang.foreign.ValueLayout

enum class DXGIAlphaMode(
	override val id: UInt
) : Mappable<DXGIAlphaMode, UInt> {
	DXGI_ALPHA_MODE_UNSPECIFIED(0u),
	DXGI_ALPHA_MODE_PREMULTIPLIED(1u),
	DXGI_ALPHA_MODE_STRAIGHT(2u),
	DXGI_ALPHA_MODE_IGNORE(3u);

	override val tag: String = name
	override fun toString(): String = stringForm()
}

val DXGI_ALPHA_MODE: ValueLayout.OfInt = ValueLayout.JAVA_INT