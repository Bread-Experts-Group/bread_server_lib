package org.bread_experts_group.ffi.windows.directx

import java.lang.foreign.MemorySegment

/**
 * Consult `typedef struct ID3D12CommandQueueVtbl` @
 * [d3d12.h](https://github.com/microsoft/DirectX-Headers/blob/main/include/directx/d3d12.h#L8765)
 */
class ID3D12CommandQueue(
	ptr: MemorySegment,
	iUnknownQueryInterfaceVTblIndex: Int,
	iUnknownAddRefVTblIndex: Int,
	iUnknownReleaseVTblIndex: Int,
	iD3D12ObjectGetPrivateDataVTblIndex: Int,
	iD3D12ObjectSetPrivateDataVTblIndex: Int,
	iD3D12ObjectSetPrivateDataInterfaceVTblIndex: Int,
	iD3D12ObjectSetNameVTblIndex: Int,
	iD3D12DeviceChildGetDeviceVTblIndex: Int,
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
		8
	)
}