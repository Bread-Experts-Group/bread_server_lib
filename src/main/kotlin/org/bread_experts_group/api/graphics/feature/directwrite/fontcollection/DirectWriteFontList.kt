package org.bread_experts_group.api.graphics.feature.directwrite.fontcollection

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.nativeLinker
import org.bread_experts_group.ffi.threadLocalPTR
import org.bread_experts_group.ffi.windows.HRESULT
import org.bread_experts_group.ffi.windows.directwrite.PIDWriteFontCollection
import org.bread_experts_group.ffi.windows.directx.IUnknown
import org.bread_experts_group.ffi.windows.tryThrowWin32Error
import org.bread_experts_group.ffi.windows.`void*`
import java.lang.foreign.MemorySegment
import java.lang.invoke.MethodHandle

open class DirectWriteFontList(
	ptr: MemorySegment
) : IUnknown(
	ptr
) {
	private var getFontCollection: (MemorySegment) -> Int = {
		val nativeGetFontCollection: MethodHandle = getLocalVTblAddress(
			DirectWriteFontList::class.java, 0
		).getDowncall(
			nativeLinker,
			HRESULT,
			`void*`.withName("this"),
			PIDWriteFontCollection.withName("fontCollection")
		)
		getFontCollection = {
			nativeGetFontCollection.invokeExact(ptr, it) as Int
		}
		nativeGetFontCollection.invokeExact(ptr, it) as Int
	}

	fun getFontCollection(): DirectWriteFontCollection {
		tryThrowWin32Error(getFontCollection(threadLocalPTR))
		return DirectWriteFontCollection(threadLocalPTR.get(`void*`, 0))
	}
}