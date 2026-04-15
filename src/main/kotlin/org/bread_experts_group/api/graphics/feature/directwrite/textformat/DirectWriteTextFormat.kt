package org.bread_experts_group.api.graphics.feature.directwrite.textformat

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.nativeLinker
import org.bread_experts_group.ffi.windows.*
import org.bread_experts_group.ffi.windows.directx.IUnknown
import java.lang.foreign.MemorySegment
import java.lang.invoke.MethodHandle

open class DirectWriteTextFormat(
	handle: MemorySegment
) : IUnknown(
	handle
) {
	var getLineSpacing: (MemorySegment, MemorySegment, MemorySegment) -> Int = { lSM, lS, b ->
		val nativeGetLineSpacing: MethodHandle = getLocalVTblAddress(
			DirectWriteTextFormat::class.java, 15
		).getDowncall(
			nativeLinker,
			HRESULT,
			`void*`.withName("this"),
			PDWORD.withName("lineSpacingMethod"),
			PFLOAT.withName("lineSpacing"),
			PFLOAT.withName("baseline")
		)
		getLineSpacing = { lSM, lS, b ->
			nativeGetLineSpacing.invokeExact(ptr, lSM, lS, b) as Int
		}
		nativeGetLineSpacing.invokeExact(ptr, lSM, lS, b) as Int
	}

	fun getLineSpacing(): TextFormatLineSpacingInformation {
		tryThrowWin32Error(
			getLineSpacing(
				threadLocalDWORD0,
				threadLocalDWORD1,
				threadLocalDWORD2
			)
		)
		return TextFormatLineSpacingInformation(
			threadLocalDWORD1.get(FLOAT, 0),
			threadLocalDWORD2.get(FLOAT, 0)
		)
	}

	var getFontSize: () -> Float = {
		val nativeGetFontSize: MethodHandle = getLocalVTblAddress(
			DirectWriteTextFormat::class.java, 22
		).getDowncall(
			nativeLinker,
			FLOAT,
			`void*`.withName("this")
		)
		getFontSize = {
			nativeGetFontSize.invokeExact(ptr) as Float
		}
		nativeGetFontSize.invokeExact(ptr) as Float
	}
}