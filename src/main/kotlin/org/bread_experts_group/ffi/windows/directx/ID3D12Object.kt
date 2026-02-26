package org.bread_experts_group.ffi.windows.directx

import java.lang.foreign.MemorySegment

open class ID3D12Object(
	ptr: MemorySegment
) : IUnknown(
	ptr
) {
	var getPrivateData = {
		TODO("Not yet implemented")
	}

	var setPrivateData = {
		TODO("Not yet implemented")
	}

	var setPrivateDataInterface = {
		TODO("Not yet implemented")
	}

	var setName = {
		TODO("Not yet implemented")
	}
}