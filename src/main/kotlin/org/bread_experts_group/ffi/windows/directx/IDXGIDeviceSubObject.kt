package org.bread_experts_group.ffi.windows.directx

import java.lang.foreign.MemorySegment

open class IDXGIDeviceSubObject(
	ptr: MemorySegment,
	iUnknownQueryInterfaceVTblIndex: Int,
	iUnknownAddRefVTblIndex: Int,
	iUnknownReleaseVTblIndex: Int,
	iDXGIObjectSetPrivateDataVTblIndex: Int,
	iDXGIObjectSetPrivateDataInterfaceVTblIndex: Int,
	iDXGIObjectGetPrivateDataVTblIndex: Int,
	iDXGIObjectGetParentVTblIndex: Int,
	getDeviceVTblIndex: Int,
	vTblReinterpretationLength: Int
) : IDXGIObject(
	ptr,
	iUnknownQueryInterfaceVTblIndex,
	iUnknownAddRefVTblIndex,
	iUnknownReleaseVTblIndex,
	iDXGIObjectSetPrivateDataVTblIndex,
	iDXGIObjectSetPrivateDataInterfaceVTblIndex,
	iDXGIObjectGetPrivateDataVTblIndex,
	iDXGIObjectGetParentVTblIndex,
	vTblReinterpretationLength
) {
	var getDevice = {
		TODO("Not yet implemented")
	}
}