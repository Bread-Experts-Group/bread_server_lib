package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.ffi.windows.vtblFunctionCounts
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

open class IUnknown(
	val ptr: MemorySegment
) {
	private val totalFunctions: Int = run {
		var total = 0
		var currentClass: Class<*> = this::class.java
		do {
			total += vtblFunctionCounts[currentClass]!!
			currentClass = currentClass.superclass
		} while (currentClass != Any::class.java)
		total
	}

	private val addressSize = ValueLayout.ADDRESS.byteSize()
	private val vTbl: MemorySegment = ptr
		.reinterpret(addressSize)
		.get(ValueLayout.ADDRESS, 0)
		.reinterpret((totalFunctions + 3) * addressSize)

	fun getVTblAddress(index: Int): MemorySegment = vTbl.get(ValueLayout.ADDRESS, index * addressSize)
	fun getLocalVTblAddress(
		localClass: Class<*>, index: Int
	): MemorySegment = getVTblAddress(
		index + run {
			var local = 0
			var currentClass: Class<*> = localClass.superclass
			while (currentClass != Any::class.java) {
				local += vtblFunctionCounts[currentClass]!!
				currentClass = currentClass.superclass
			}
			local
		}
	)

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