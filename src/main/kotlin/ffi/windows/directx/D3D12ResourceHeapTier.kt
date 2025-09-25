package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.coder.Mappable
import java.lang.foreign.ValueLayout

enum class D3D12ResourceHeapTier(
	override val id: UInt
) : Mappable<D3D12ResourceHeapTier, UInt> {
	D3D12_RESOURCE_HEAP_TIER_1(1u),
	D3D12_RESOURCE_HEAP_TIER_2(2u);

	override val tag: String = name
	override fun toString(): String = stringForm()
}

val D3D12_RESOURCE_HEAP_TIER: ValueLayout.OfInt = ValueLayout.JAVA_INT