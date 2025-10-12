package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.Mappable
import java.lang.foreign.ValueLayout

enum class D3D12CrossNodeSharingTier(
	override val id: UInt
) : Mappable<D3D12CrossNodeSharingTier, UInt> {
	D3D12_CROSS_NODE_SHARING_TIER_NOT_SUPPORTED(0u),
	D3D12_CROSS_NODE_SHARING_TIER_1_EMULATED(1u),
	D3D12_CROSS_NODE_SHARING_TIER_1(2u),
	D3D12_CROSS_NODE_SHARING_TIER_2(3u),
	D3D12_CROSS_NODE_SHARING_TIER_3(4u);

	override val tag: String = name
	override fun toString(): String = stringForm()
}

val D3D12_CROSS_NODE_SHARING_TIER: ValueLayout.OfInt = ValueLayout.JAVA_INT