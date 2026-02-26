package org.bread_experts_group.ffi.windows.directx

import java.lang.foreign.MemorySegment

/**
 * Consult `typedef struct ID3D12CommandQueueVtbl` @
 * [d3d12.h](https://github.com/microsoft/DirectX-Headers/blob/main/include/directx/d3d12.h#L8765)
 */
class ID3D12CommandQueue(
	ptr: MemorySegment,
	totalFunctions: Int
) : ID3D12Pageable(
	ptr
)