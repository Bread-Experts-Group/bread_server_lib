package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.Mappable
import java.lang.foreign.ValueLayout

enum class D3D12ResourceBindingTier(
	override val id: UInt
) : Mappable<D3D12ResourceBindingTier, UInt> {
	D3D12_RESOURCE_BINDING_TIER_1(1u),
	D3D12_RESOURCE_BINDING_TIER_2(2u),
	D3D12_RESOURCE_BINDING_TIER_3(3u);

	override val tag: String = name
	override fun toString(): String = stringForm()
}

val D3D12_RESOURCE_BINDING_TIER: ValueLayout.OfInt = ValueLayout.JAVA_INT