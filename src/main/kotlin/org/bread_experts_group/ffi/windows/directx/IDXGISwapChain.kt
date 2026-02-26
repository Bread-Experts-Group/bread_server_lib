package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.nativeLinker
import org.bread_experts_group.ffi.windows.HRESULT
import org.bread_experts_group.generic.Flaggable.Companion.raw
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.util.*

open class IDXGISwapChain(
	ptr: MemorySegment
) : IDXGIDeviceSubObject(
	ptr
) {
	var present: (Int, EnumSet<DXGIPresent>) -> Int = { s, f ->
		val handle = getLocalVTblAddress(
			IDXGISwapChain::class.java, 0
		).getDowncall(
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