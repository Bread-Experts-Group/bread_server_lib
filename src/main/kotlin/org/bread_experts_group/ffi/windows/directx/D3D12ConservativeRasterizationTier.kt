package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.Mappable
import java.lang.foreign.ValueLayout

enum class D3D12ConservativeRasterizationTier(
	override val id: UInt
) : Mappable<D3D12ConservativeRasterizationTier, UInt> {
	D3D12_CONSERVATIVE_RASTERIZATION_TIER_NOT_SUPPORTED(0u),
	D3D12_CONSERVATIVE_RASTERIZATION_TIER_1(1u),
	D3D12_CONSERVATIVE_RASTERIZATION_TIER_2(2u),
	D3D12_CONSERVATIVE_RASTERIZATION_TIER_3(3u);

	override val tag: String = name
	override fun toString(): String = stringForm()
}

val D3D12_CONSERVATIVE_RASTERIZATION_TIER: ValueLayout.OfInt = ValueLayout.JAVA_INT