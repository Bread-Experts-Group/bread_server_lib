package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.nativeLinker
import org.bread_experts_group.ffi.windows.HRESULT
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

open class IDXGIFactory2(
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
	iDXGIFactory1EnumAdapters1VTblIndex: Int,
	iDXGIFactory1IsCurrentVTblIndex: Int,
	isWindowedStereoEnabledVTblIndex: Int,
	createSwapChainForHwndVTblIndex: Int,
	createSwapChainForCoreWindowVTblIndex: Int,
	getSharedResourceAdapterLuidVTblIndex: Int,
	registerStereoStatusWindowVTblIndex: Int,
	registerStereoStatusEventVTblIndex: Int,
	unregisterStereoStatusVTblIndex: Int,
	registerOcclusionStatusWindowVTblIndex: Int,
	registerOcclusionStatusEventVTblIndex: Int,
	unregisterOcclusionStatusVTblIndex: Int,
	createSwapChainForCompositionVTblIndex: Int,
	vTblReinterpretationLength: Int
) : IDXGIFactory1(
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
	iDXGIFactory1EnumAdapters1VTblIndex,
	iDXGIFactory1IsCurrentVTblIndex,
	vTblReinterpretationLength
) {
	var isWindowedStereoEnabled = {
		TODO("Not yet implemented")
	}

	var createSwapChainForHwnd: (
		MemorySegment, MemorySegment, MemorySegment, MemorySegment, MemorySegment, MemorySegment
	) -> Int = { p, h, pD, pF, pR, pp ->
		val handle = getVTblAddress(createSwapChainForHwndVTblIndex).getDowncall(
			nativeLinker, HRESULT,
			ValueLayout.ADDRESS,
			ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS,
			ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS
		)
		this.createSwapChainForHwnd = { p, h, pD, pF, pR, pp ->
			handle.invokeExact(ptr, p, h, pD, pF, pR, pp) as Int
		}
		handle.invokeExact(ptr, p, h, pD, pF, pR, pp) as Int
	}

	var createSwapChainForCoreWindow = {
		TODO("Not yet implemented")
	}

	var getSharedResourceAdapterLuid = {
		TODO("Not yet implemented")
	}

	var registerStereoStatusWindow = {
		TODO("Not yet implemented")
	}

	var registerStereoStatusEvent = {
		TODO("Not yet implemented")
	}

	var unregisterStereoStatus = {
		TODO("Not yet implemented")
	}

	var registerOcclusionStatusWindow = {
		TODO("Not yet implemented")
	}

	var registerOcclusionStatusEvent = {
		TODO("Not yet implemented")
	}

	var unregisterOcclusionStatus = {
		TODO("Not yet implemented")
	}

	var createSwapChainForComposition = {
		TODO("Not yet implemented")
	}
}