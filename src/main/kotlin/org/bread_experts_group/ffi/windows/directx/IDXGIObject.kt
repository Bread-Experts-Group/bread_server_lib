package org.bread_experts_group.ffi.windows.directx

import java.lang.foreign.MemorySegment

open class IDXGIObject(
	ptr: MemorySegment
) : IUnknown(
	ptr
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