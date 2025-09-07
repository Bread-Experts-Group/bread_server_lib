package org.bread_experts_group.api.graphics.windows.window

import org.bread_experts_group.api.graphics.feature.window.GraphicsWindow
import org.bread_experts_group.ffi.windows.decodeLastError
import org.bread_experts_group.ffi.windows.nativeCreateWindowExW
import org.bread_experts_group.ffi.windows.nativeGetModuleHandleW
import org.bread_experts_group.ffi.windows.stringToPCWSTR
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsGraphicsWindow(template: WindowsGraphicsWindowTemplate) : GraphicsWindow() {
	private val arena: Arena = Arena.ofConfined()

	init {
		val localHandle = nativeGetModuleHandleW.invokeExact(MemorySegment.NULL) as MemorySegment
		val window = nativeCreateWindowExW.invokeExact(
			0x00000300,
			MemorySegment.ofAddress(template.classAtom.toLong() and 0xFFFF),
			stringToPCWSTR(arena, "$"),
			0x10CC0000,
			0, 0,
			300, 400,
			MemorySegment.NULL,
			MemorySegment.NULL,
			localHandle,
			MemorySegment.NULL
		) as MemorySegment
		if (window == MemorySegment.NULL) decodeLastError(arena)
	}
}