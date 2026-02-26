package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.ffi.getDowncallVoid
import org.bread_experts_group.ffi.nativeLinker
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

/**
 * Consult `typedef struct ID3D12DebugVtbl` @
 * [d3d12sdklayers.h](https://github.com/microsoft/DirectX-Headers/blob/main/include/directx/d3d12sdklayers.h)
 */
class ID3D12Debug(
	ptr: MemorySegment
) : IUnknown(
	ptr
) {
	var enableDebugLayer: () -> Unit = {
		val handle = getLocalVTblAddress(
			ID3D12Debug::class.java, 0
		).getDowncallVoid(
			nativeLinker, ValueLayout.ADDRESS
		)
		this.enableDebugLayer = { handle.invokeExact(ptr) }
		handle.invokeExact(ptr)
	}
}