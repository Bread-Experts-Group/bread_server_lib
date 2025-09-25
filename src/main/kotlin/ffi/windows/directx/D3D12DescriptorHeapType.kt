package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.coder.Mappable
import java.lang.foreign.ValueLayout

enum class D3D12DescriptorHeapType(override val id: UInt) : Mappable<D3D12DescriptorHeapType, UInt> {
	D3D12_DESCRIPTOR_HEAP_TYPE_CBV_SRV_UAV(0u),
	D3D12_DESCRIPTOR_HEAP_TYPE_SAMPLER(D3D12_DESCRIPTOR_HEAP_TYPE_CBV_SRV_UAV.id + 1u),
	D3D12_DESCRIPTOR_HEAP_TYPE_RTV(D3D12_DESCRIPTOR_HEAP_TYPE_SAMPLER.id + 1u),
	D3D12_DESCRIPTOR_HEAP_TYPE_DSV(D3D12_DESCRIPTOR_HEAP_TYPE_RTV.id + 1u),
	D3D12_DESCRIPTOR_HEAP_TYPE_NUM_TYPES(D3D12_DESCRIPTOR_HEAP_TYPE_DSV.id + 1u);

	override val tag: String = name
	override fun toString(): String = stringForm()
}

val D3D12_DESCRIPTOR_HEAP_TYPE: ValueLayout.OfInt = ValueLayout.JAVA_INT