package org.bread_experts_group.ffi.windows.directx

import java.lang.foreign.MemorySegment

open class IDXGIFactory(
	ptr: MemorySegment
) : IDXGIObject(
	ptr
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