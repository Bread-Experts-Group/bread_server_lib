package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.generic.Flaggable.Companion.raw
import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.nativeLinker
import org.bread_experts_group.ffi.windows.HRESULT
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.util.*

open class IDXGISwapChain(
	ptr: MemorySegment,
	iUnknownQueryInterfaceVTblIndex: Int,
	iUnknownAddRefVTblIndex: Int,
	iUnknownReleaseVTblIndex: Int,
	iDXGIObjectSetPrivateDataVTblIndex: Int,
	iDXGIObjectSetPrivateDataInterfaceVTblIndex: Int,
	iDXGIObjectGetPrivateDataVTblIndex: Int,
	iDXGIObjectGetParentVTblIndex: Int,
	iDXGIDeviceSubObjectGetDeviceVTblIndex: Int,
	presentVTblIndex: Int,
	getBufferVTblIndex: Int,
	setFullscreenStateVTblIndex: Int,
	getFullscreenStateVTblIndex: Int,
	getDescVTblIndex: Int,
	resizeBuffersVTblIndex: Int,
	resizeTargetVTblIndex: Int,
	getContainingOutputVTblIndex: Int,
	getFrameStatisticsVTblIndex: Int,
	getLastPresentCountVTblIndex: Int,
	vTblReinterpretationLength: Int
) : IDXGIDeviceSubObject(
	ptr,
	iUnknownQueryInterfaceVTblIndex,
	iUnknownAddRefVTblIndex,
	iUnknownReleaseVTblIndex,
	iDXGIObjectSetPrivateDataVTblIndex,
	iDXGIObjectSetPrivateDataInterfaceVTblIndex,
	iDXGIObjectGetPrivateDataVTblIndex,
	iDXGIObjectGetParentVTblIndex,
	iDXGIDeviceSubObjectGetDeviceVTblIndex,
	vTblReinterpretationLength
) {
	var present: (Int, EnumSet<DXGIPresent>) -> Int = { s, f ->
		val handle = getVTblAddress(presentVTblIndex).getDowncall(
			nativeLinker, HRESULT,
			ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT
		)
		this.present = { s, f -> handle.invokeExact(ptr, s, f.raw().toInt()) as Int }
		handle.invokeExact(ptr, s, f.raw().toInt()) as Int
	}

	var getBuffer = {
		TODO("Not yet implemented")
	}

	var setFullscreenState = {
		TODO("Not yet implemented")
	}

	var getFullscreenState = {
		TODO("Not yet implemented")
	}

	var getDesc = {
		TODO("Not yet implemented")
	}

	var resizeBuffers = {
		TODO("Not yet implemented")
	}

	var resizeTarget = {
		TODO("Not yet implemented")
	}

	var getContainingOutput = {
		TODO("Not yet implemented")
	}

	var getFrameStatistics = {
		TODO("Not yet implemented")
	}

	var getLastPresentCount = {
		TODO("Not yet implemented")
	}
}