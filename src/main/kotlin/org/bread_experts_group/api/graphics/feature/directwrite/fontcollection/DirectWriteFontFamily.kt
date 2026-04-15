package org.bread_experts_group.api.graphics.feature.directwrite.fontcollection

import org.bread_experts_group.api.graphics.feature.directwrite.textformat.DirectWriteFontWeight
import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.nativeLinker
import org.bread_experts_group.ffi.threadLocalPTR
import org.bread_experts_group.ffi.windows.HRESULT
import org.bread_experts_group.ffi.windows.directwrite.*
import org.bread_experts_group.ffi.windows.tryThrowWin32Error
import org.bread_experts_group.ffi.windows.`void*`
import java.lang.foreign.MemorySegment
import java.lang.invoke.MethodHandle

class DirectWriteFontFamily(
	ptr: MemorySegment
) : DirectWriteFontList(
	ptr
) {
	private var getFirstMatchingFont: (Int, Int, Int, MemorySegment) -> Int = { w, s, sT, mF ->
		val nativeGetFirstMatchingFont: MethodHandle = getLocalVTblAddress(
			DirectWriteFontFamily::class.java, 1
		).getDowncall(
			nativeLinker,
			HRESULT,
			`void*`.withName("this"),
			DWRITE_FONT_WEIGHT.withName("weight"),
			DWRITE_FONT_STRETCH.withName("stretch"),
			DWRITE_FONT_STYLE.withName("style"),
			PIDWriteFont.withName("matchingFont")
		)
		getFirstMatchingFont = { w, s, sT, mF ->
			nativeGetFirstMatchingFont.invokeExact(ptr, w, s, sT, mF) as Int
		}
		nativeGetFirstMatchingFont.invokeExact(ptr, w, s, sT, mF) as Int
	}

	fun getFirstMatchingFont(
		weight: DirectWriteFontWeight,
		stretch: DWriteFontStretch,
		style: DWriteFontStyle
	): DirectWriteFont {
		tryThrowWin32Error(
			getFirstMatchingFont(
				weight.weight,
				stretch.id,
				style.id,
				threadLocalPTR
			)
		)
		return DirectWriteFont(threadLocalPTR.get(`void*`, 0))
	}
}