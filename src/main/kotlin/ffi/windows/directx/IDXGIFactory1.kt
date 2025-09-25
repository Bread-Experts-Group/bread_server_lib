package org.bread_experts_group.ffi.windows.directx

import java.lang.foreign.MemorySegment

open class IDXGIFactory1(
	ptr: MemorySegment,
	iUnknownQueryInterfaceVTblIndex: Int,
	iUnknownAddRefVTblIndex: Int,
	iUnknownReleaseVTblIndex: Int,
	iDXGIObjectSetPrivateDataVTblIndex: Int,
	iDXGIObjectSetPrivateDataInterfaceVTblIndex: Int,
	iDXGIObjectGetPrivateDataVTblIndex: Int,
	iDXGIObjectGetParentVTblIndex: Int,
	iDXGIFactoryEnumAdaptersVTblIndex: Int,
	iDXGIFactoryMakeWindowAssociationVTblIndex: Int,
	iDXGIFactoryGetWindowAssociationVTblIndex: Int,
	iDXGIFactoryCreateSwapChainVTblIndex: Int,
	iDXGIFactoryCreateSoftwareAdapterVTblIndex: Int,
	enumAdapters1VTblIndex: Int,
	isCurrentVTblIndex: Int,
	vTblReinterpretationLength: Int
) : IDXGIFactory(
	ptr,
	iUnknownQueryInterfaceVTblIndex,
	iUnknownAddRefVTblIndex,
	iUnknownReleaseVTblIndex,
	iDXGIObjectSetPrivateDataVTblIndex,
	iDXGIObjectSetPrivateDataInterfaceVTblIndex,
	iDXGIObjectGetPrivateDataVTblIndex,
	iDXGIObjectGetParentVTblIndex,
	iDXGIFactoryEnumAdaptersVTblIndex,
	iDXGIFactoryMakeWindowAssociationVTblIndex,
	iDXGIFactoryGetWindowAssociationVTblIndex,
	iDXGIFactoryCreateSwapChainVTblIndex,
	iDXGIFactoryCreateSoftwareAdapterVTblIndex,
	vTblReinterpretationLength
) {
	var enumAdapters1 = {
		TODO("Not yet implemented")
	}

	var isCurrent = {
		TODO("Not yet implemented")
	}
}