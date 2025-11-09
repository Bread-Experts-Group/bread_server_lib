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
	ptr: MemorySegment,
	iUnknownQueryInterfaceVTblIndex: Int,
	iUnknownAddRefVTblIndex: Int,
	iUnknownReleaseVTblIndex: Int,
	enableDebugLayerVTblIndex: Int,
	vTblReinterpretationLength: Int
) : IUnknown(
	ptr,
	iUnknownQueryInterfaceVTblIndex,
	iUnknownAddRefVTblIndex,
	iUnknownReleaseVTblIndex,
	vTblReinterpretationLength
) {
	constructor(ptr: MemorySegment) : this(
		ptr,
		0,
		1,
		2,
		3,
		4
	)

	var enableDebugLayer: () -> Unit = {
		val handle = getVTblAddress(enableDebugLayerVTblIndex).getDowncallVoid(
			nativeLinker, ValueLayout.ADDRESS
		)
		this.enableDebugLayer = { handle.invokeExact(ptr) }
		handle.invokeExact(ptr)
	}
}