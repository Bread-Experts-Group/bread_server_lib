package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.windows.UINT
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class IDXGISwapChain3(
	ptr: MemorySegment,
	iUnknownQueryInterfaceVTblIndex: Int,
	iUnknownAddRefVTblIndex: Int,
	iUnknownReleaseVTblIndex: Int,
	iDXGIObjectSetPrivateDataVTblIndex: Int,
	iDXGIObjectSetPrivateDataInterfaceVTblIndex: Int,
	iDXGIObjectGetPrivateDataVTblIndex: Int,
	iDXGIObjectGetParentVTblIndex: Int,
	iDXGIDeviceSubObjectGetDeviceVTblIndex: Int,
	iDXGISwapChainPresentVTblIndex: Int,
	iDXGISwapChainGetBufferVTblIndex: Int,
	iDXGISwapChainSetFullscreenStateVTblIndex: Int,
	iDXGISwapChainGetFullscreenStateVTblIndex: Int,
	iDXGISwapChainGetDescVTblIndex: Int,
	iDXGISwapChainResizeBuffersVTblIndex: Int,
	iDXGISwapChainResizeTargetVTblIndex: Int,
	iDXGISwapChainGetContainingOutputVTblIndex: Int,
	iDXGISwapChainGetFrameStatisticsVTblIndex: Int,
	iDXGISwapChainGetLastPresentCountVTblIndex: Int,
	iDXGISwapChain1GetDesc1VTblIndex: Int,
	iDXGISwapChain1GetFullscreenDescVTblIndex: Int,
	iDXGISwapChain1GetHwndVTblIndex: Int,
	iDXGISwapChain1GetCoreWindowVTblIndex: Int,
	iDXGISwapChain1Present1VTblIndex: Int,
	iDXGISwapChain1IsTemporaryMonoSupportedVTblIndex: Int,
	iDXGISwapChain1GetRestrictToOutputVTblIndex: Int,
	iDXGISwapChain1SetBackgroundColorVTblIndex: Int,
	iDXGISwapChain1GetBackgroundColorVTblIndex: Int,
	iDXGISwapChain1SetRotationVTblIndex: Int,
	iDXGISwapChain1GetRotationVTblIndex: Int,
	iDXGISwapChain2SetSourceSizeVTblIndex: Int,
	iDXGISwapChain2GetSourceSizeVTblIndex: Int,
	iDXGISwapChain2SetMaximumFrameLatencyVTblIndex: Int,
	iDXGISwapChain2GetMaximumFrameLatencyVTblIndex: Int,
	iDXGISwapChain2GetFrameLatencyWaitableObjectVTblIndex: Int,
	iDXGISwapChain2SetMatrixTransformVTblIndex: Int,
	iDXGISwapChain2GetMatrixTransformVTblIndex: Int,
	getCurrentBackBufferIndexVTblIndex: Int,
	checkColorSpaceSupportVTblIndex: Int,
	setColorSpace1VTblIndex: Int,
	resizeBuffers1VTblIndex: Int,
	vTblReinterpretationLength: Int
) : IDXGISwapChain2(
	ptr,
	iUnknownQueryInterfaceVTblIndex,
	iUnknownAddRefVTblIndex,
	iUnknownReleaseVTblIndex,
	iDXGIObjectSetPrivateDataVTblIndex,
	iDXGIObjectSetPrivateDataInterfaceVTblIndex,
	iDXGIObjectGetPrivateDataVTblIndex,
	iDXGIObjectGetParentVTblIndex,
	iDXGIDeviceSubObjectGetDeviceVTblIndex,
	iDXGISwapChainPresentVTblIndex,
	iDXGISwapChainGetBufferVTblIndex,
	iDXGISwapChainSetFullscreenStateVTblIndex,
	iDXGISwapChainGetFullscreenStateVTblIndex,
	iDXGISwapChainGetDescVTblIndex,
	iDXGISwapChainResizeBuffersVTblIndex,
	iDXGISwapChainResizeTargetVTblIndex,
	iDXGISwapChainGetContainingOutputVTblIndex,
	iDXGISwapChainGetFrameStatisticsVTblIndex,
	iDXGISwapChainGetLastPresentCountVTblIndex,
	iDXGISwapChain1GetDesc1VTblIndex,
	iDXGISwapChain1GetFullscreenDescVTblIndex,
	iDXGISwapChain1GetHwndVTblIndex,
	iDXGISwapChain1GetCoreWindowVTblIndex,
	iDXGISwapChain1Present1VTblIndex,
	iDXGISwapChain1IsTemporaryMonoSupportedVTblIndex,
	iDXGISwapChain1GetRestrictToOutputVTblIndex,
	iDXGISwapChain1SetBackgroundColorVTblIndex,
	iDXGISwapChain1GetBackgroundColorVTblIndex,
	iDXGISwapChain1SetRotationVTblIndex,
	iDXGISwapChain1GetRotationVTblIndex,
	iDXGISwapChain2SetSourceSizeVTblIndex,
	iDXGISwapChain2GetSourceSizeVTblIndex,
	iDXGISwapChain2SetMaximumFrameLatencyVTblIndex,
	iDXGISwapChain2GetMaximumFrameLatencyVTblIndex,
	iDXGISwapChain2GetFrameLatencyWaitableObjectVTblIndex,
	iDXGISwapChain2SetMatrixTransformVTblIndex,
	iDXGISwapChain2GetMatrixTransformVTblIndex,
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
		11,
		12,
		13,
		14,
		15,
		16,
		17,
		18,
		19,
		20,
		21,
		22,
		23,
		24,
		25,
		26,
		27,
		28,
		29,
		30,
		31,
		32,
		33,
		34,
		35,
		36,
		37,
		38,
		39,
		40
	)

	var getCurrentBackBufferIndex: () -> UInt = {
		val handle = getVTblAddress(getCurrentBackBufferIndexVTblIndex).getDowncall(
			linker, UINT,
			ValueLayout.ADDRESS
		)
		this.getCurrentBackBufferIndex = { (handle.invokeExact(ptr) as Int).toUInt() }
		(handle.invokeExact(ptr) as Int).toUInt()
	}

	var checkColorSpaceSupport = {
		TODO("Not yet implemented")
	}

	var setColorSpace1 = {
		TODO("Not yet implemented")
	}

	var resizeBuffers1 = {
		TODO("Not yet implemented")
	}
}