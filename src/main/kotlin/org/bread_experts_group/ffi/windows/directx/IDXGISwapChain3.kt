package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.nativeLinker
import org.bread_experts_group.ffi.windows.UINT
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class IDXGISwapChain3(
	ptr: MemorySegment
) : IDXGISwapChain2(
	ptr
) {
	var getCurrentBackBufferIndex: () -> UInt = {
		val handle = getLocalVTblAddress(
			IDXGISwapChain3::class.java, 0
		).getDowncall(
			nativeLinker, UINT,
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