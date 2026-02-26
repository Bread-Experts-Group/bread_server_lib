package org.bread_experts_group.ffi.windows.directx

import java.lang.foreign.MemorySegment

open class IDXGIDeviceSubObject(
	ptr: MemorySegment
) : IDXGIObject(
	ptr
) {
	var getDevice = {
		TODO("Not yet implemented")
	}
}