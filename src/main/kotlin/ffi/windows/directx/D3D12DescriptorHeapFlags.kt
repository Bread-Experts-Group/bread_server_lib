package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.coder.Flaggable
import java.lang.foreign.ValueLayout

enum class D3D12DescriptorHeapFlags(override val position: Long) : Flaggable {
	D3D12_DESCRIPTOR_HEAP_FLAG_SHADER_VISIBLE(0x1)
}

val D3D12_DESCRIPTOR_HEAP_FLAGS: ValueLayout.OfInt = ValueLayout.JAVA_INT