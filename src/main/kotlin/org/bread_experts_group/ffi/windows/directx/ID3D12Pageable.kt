package org.bread_experts_group.ffi.windows.directx

import java.lang.foreign.MemorySegment

open class ID3D12Pageable(
	ptr: MemorySegment
) : ID3D12DeviceChild(
	ptr
)