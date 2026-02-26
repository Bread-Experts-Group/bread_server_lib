package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.nativeLinker
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class ID3D12DescriptorHeap(
	ptr: MemorySegment
) : ID3D12Pageable(
	ptr
) {
	var getDesc = {
		TODO("Not yet implemented")
	}

	var getCPUDescriptorHandleForHeapStart: (MemorySegment) -> MemorySegment = { r ->
		val handle = getLocalVTblAddress(
			ID3D12DescriptorHeap::class.java, 1
		).getDowncall(
			nativeLinker, ValueLayout.ADDRESS,
			ValueLayout.ADDRESS, ValueLayout.ADDRESS
		)
		this.getCPUDescriptorHandleForHeapStart = { r -> handle.invokeExact(ptr, r) as MemorySegment }
		handle.invokeExact(ptr, r) as MemorySegment
	}

	var getGPUDescriptorHandleForHeapStart = {
		TODO("Not yet implemented")
	}
}