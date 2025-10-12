package org.bread_experts_group.ffi.windows.directx

import java.lang.foreign.MemorySegment

open class IDXGIFactory(
	ptr: MemorySegment,
	iUnknownQueryInterfaceVTblIndex: Int,
	iUnknownAddRefVTblIndex: Int,
	iUnknownReleaseVTblIndex: Int,
	iDXGIObjectSetPrivateDataVTblIndex: Int,
	iDXGIObjectSetPrivateDataInterfaceVTblIndex: Int,
	iDXGIObjectGetPrivateDataVTblIndex: Int,
	iDXGIObjectGetParentVTblIndex: Int,
	enumAdaptersVTblIndex: Int,
	makeWindowAssociationVTblIndex: Int,
	getWindowAssociationVTblIndex: Int,
	createSwapChainVTblIndex: Int,
	createSoftwareAdapterVTblIndex: Int,
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
	var enumAdapters = {
		TODO("Not yet implemented")
	}

	var makeWindowAssociation = {
		TODO("Not yet implemented")
	}

	var getWindowAssociation = {
		TODO("Not yet implemented")
	}

	var createSwapChain = {
		TODO("Not yet implemented")
	}

	var createSoftwareAdapter = {
		TODO("Not yet implemented")
	}
}