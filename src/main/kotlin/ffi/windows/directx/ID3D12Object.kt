package org.bread_experts_group.ffi.windows.directx

import java.lang.foreign.MemorySegment

open class ID3D12Object(
	ptr: MemorySegment,
	iUnknownQueryInterfaceVTblIndex: Int,
	iUnknownAddRefVTblIndex: Int,
	iUnknownReleaseVTblIndex: Int,
	getPrivateDataVTblIndex: Int,
	setPrivateDataVTblIndex: Int,
	setPrivateDataInterfaceVTblIndex: Int,
	setNameVTblIndex: Int,
	vTblReinterpretationLength: Int
) : IUnknown(
	ptr,
	iUnknownQueryInterfaceVTblIndex,
	iUnknownAddRefVTblIndex,
	iUnknownReleaseVTblIndex,
	vTblReinterpretationLength
) {
	var getPrivateData = {
		TODO("Not yet implemented")
	}

	var setPrivateData = {
		TODO("Not yet implemented")
	}

	var setPrivateDataInterface = {
		TODO("Not yet implemented")
	}

	var setName = {
		TODO("Not yet implemented")
	}
}