package org.bread_experts_group.ffi.windows.directx

import java.lang.foreign.MemorySegment

open class ID3D12DeviceChild(
	ptr: MemorySegment,
	iUnknownQueryInterfaceVTblIndex: Int,
	iUnknownAddRefVTblIndex: Int,
	iUnknownReleaseVTblIndex: Int,
	iD3D12ObjectGetPrivateDataVTblIndex: Int,
	iD3D12ObjectSetPrivateDataVTblIndex: Int,
	iD3D12ObjectSetPrivateDataInterfaceVTblIndex: Int,
	iD3D12ObjectSetNameVTblIndex: Int,
	getDeviceVTblIndex: Int,
	vTblReinterpretationLength: Int
) : ID3D12Object(
	ptr,
	iUnknownQueryInterfaceVTblIndex,
	iUnknownAddRefVTblIndex,
	iUnknownReleaseVTblIndex,
	iD3D12ObjectGetPrivateDataVTblIndex,
	iD3D12ObjectSetPrivateDataVTblIndex,
	iD3D12ObjectSetPrivateDataInterfaceVTblIndex,
	iD3D12ObjectSetNameVTblIndex,
	vTblReinterpretationLength
) {
	var getDevice = {
		TODO("Not yet implemented")
	}
}