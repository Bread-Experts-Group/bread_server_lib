package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.nativeLinker
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class ID3D12DescriptorHeap(
	ptr: MemorySegment,
	iUnknownQueryInterfaceVTblIndex: Int,
	iUnknownAddRefVTblIndex: Int,
	iUnknownReleaseVTblIndex: Int,
	iD3D12ObjectGetPrivateDataVTblIndex: Int,
	iD3D12ObjectSetPrivateDataVTblIndex: Int,
	iD3D12ObjectSetPrivateDataInterfaceVTblIndex: Int,
	iD3D12ObjectSetNameVTblIndex: Int,
	iD3D12DeviceChildGetDeviceVTblIndex: Int,
	getDescVTblIndex: Int,
	getCPUDescriptorHandleForHeapStartVTblIndex: Int,
	getGPUDescriptorHandleForHeapStartVTblIndex: Int,
	vTblReinterpretationLength: Int
) : ID3D12Pageable(
	ptr,
	iUnknownQueryInterfaceVTblIndex,
	iUnknownAddRefVTblIndex,
	iUnknownReleaseVTblIndex,
	iD3D12ObjectGetPrivateDataVTblIndex,
	iD3D12ObjectSetPrivateDataVTblIndex,
	iD3D12ObjectSetPrivateDataInterfaceVTblIndex,
	iD3D12ObjectSetNameVTblIndex,
	iD3D12DeviceChildGetDeviceVTblIndex,
	vTblReinterpretationLength
) {
	constructor(ptr: MemorySegment) : this(
		ptr,
		0,
		1,
		2,
		3,
		4,
		5,
		6,
		7,
		8,
		9,
		10,
		11
	)

	var getDesc = {
		TODO("Not yet implemented")
	}

	var getCPUDescriptorHandleForHeapStart: (MemorySegment) -> MemorySegment = { r ->
		val handle = getVTblAddress(getCPUDescriptorHandleForHeapStartVTblIndex).getDowncall(
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