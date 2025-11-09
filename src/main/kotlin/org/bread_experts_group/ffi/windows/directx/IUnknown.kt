package org.bread_experts_group.ffi.windows.directx

import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

open class IUnknown(
	val ptr: MemorySegment,
	queryInterfaceVTblIndex: Int,
	addRefVTblIndex: Int,
	releaseVTblIndex: Int,
	vTblReinterpretationLength: Int
) {
	private val addressSize = ValueLayout.ADDRESS.byteSize()
	private val vTbl: MemorySegment = ptr
		.reinterpret(addressSize)
		.get(ValueLayout.ADDRESS, 0)
		.reinterpret(vTblReinterpretationLength * addressSize)

	fun getVTblAddress(index: Int): MemorySegment = vTbl.get(ValueLayout.ADDRESS, index * addressSize)

	var queryInterface = {
		TODO("Not yet implemented")
	}

	var addRef = {
		TODO("Not yet implemented")
	}

	var release = {
		TODO("Not yet implemented")
	}
}