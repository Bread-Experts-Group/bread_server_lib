package org.bread_experts_group.ffi.windows.directx

import java.lang.foreign.MemorySegment

open class ID3D12DeviceChild(
	ptr: MemorySegment
) : ID3D12Object(
	ptr
) {
	var getDevice = {
		TODO("Not yet implemented")
	}
}