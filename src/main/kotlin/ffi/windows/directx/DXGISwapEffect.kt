package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.coder.Mappable
import java.lang.foreign.ValueLayout

enum class DXGISwapEffect(
	override val id: UInt
) : Mappable<DXGISwapEffect, UInt> {
	DXGI_SWAP_EFFECT_DISCARD(0u),
	DXGI_SWAP_EFFECT_SEQUENTIAL(1u),
	DXGI_SWAP_EFFECT_FLIP_SEQUENTIAL(3u),
	DXGI_SWAP_EFFECT_FLIP_DISCARD(4u);

	override val tag: String = name
	override fun toString(): String = stringForm()
}

val DXGI_SWAP_EFFECT: ValueLayout.OfInt = ValueLayout.JAVA_INT