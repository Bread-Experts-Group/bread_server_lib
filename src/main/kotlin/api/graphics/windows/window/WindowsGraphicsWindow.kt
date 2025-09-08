package org.bread_experts_group.api.graphics.windows.window

import org.bread_experts_group.api.graphics.feature.window.GraphicsWindow
import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowFeatureImplementation
import org.bread_experts_group.ffi.windows.decodeLastError
import org.bread_experts_group.ffi.windows.nativeCreateWindowExW
import org.bread_experts_group.ffi.windows.nativeGetModuleHandleW
import org.bread_experts_group.ffi.windows.nativeSendMessageW
import org.bread_experts_group.ffi.windows.stringToPCWSTR
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsGraphicsWindow(template: WindowsGraphicsWindowTemplate) : GraphicsWindow() {
	private val arena: Arena = Arena.ofConfined()
	private val hWnd: MemorySegment
	override val features: Set<GraphicsWindowFeatureImplementation<*>> = setOf(
		WindowsGraphicsWindowNameFeature(this)
	)

	internal fun sendMessage(message: Int, wParam: Long, lParam: Long): Int {
		return nativeSendMessageW.invokeExact(hWnd, message, wParam, lParam) as Int
	}

	init {
		val localHandle = nativeGetModuleHandleW.invokeExact(MemorySegment.NULL) as MemorySegment
		hWnd = nativeCreateWindowExW.invokeExact(
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
		if (hWnd == MemorySegment.NULL) decodeLastError(arena)
	}
}