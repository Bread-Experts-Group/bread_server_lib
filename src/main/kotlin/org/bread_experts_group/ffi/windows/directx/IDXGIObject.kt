package org.bread_experts_group.ffi.windows.directx

import java.lang.foreign.MemorySegment

open class IDXGIObject(
	ptr: MemorySegment,
	iUnknownQueryInterfaceVTblIndex: Int,
	iUnknownAddRefVTblIndex: Int,
	iUnknownReleaseVTblIndex: Int,
	setPrivateDataVTblIndex: Int,
	setPrivateDataInterfaceVTblIndex: Int,
	getPrivateDataVTblIndex: Int,
	getParentVTblIndex: Int,
	vTblReinterpretationLength: Int
) : IUnknown(
	ptr,
	iUnknownQueryInterfaceVTblIndex,
	iUnknownAddRefVTblIndex,
	iUnknownReleaseVTblIndex,
	vTblReinterpretationLength
) {
	var setPrivateData = {
		TODO("Not yet implemented")
	}

	var setPrivateDataInterface = {
		TODO("Not yet implemented")
	}

	var getPrivateData = {
		TODO("Not yet implemented")
	}

	var getParent = {
		TODO("Not yet implemented")
	}
}