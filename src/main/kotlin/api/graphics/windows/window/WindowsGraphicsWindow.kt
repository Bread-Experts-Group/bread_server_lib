package org.bread_experts_group.api.graphics.windows.window

import org.bread_experts_group.api.PreInitializableClosable
import org.bread_experts_group.api.graphics.feature.window.GraphicsWindow
import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowFeatureImplementation
import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowFeatures
import org.bread_experts_group.coder.Flaggable.Companion.raw
import org.bread_experts_group.ffi.windows.COMException
import org.bread_experts_group.ffi.windows.WindowsMessageTypes
import org.bread_experts_group.ffi.windows.WindowsPixelFormatDescriptorFlags
import org.bread_experts_group.ffi.windows.WindowsPixelTypes
import org.bread_experts_group.ffi.windows.decodeLastError
import org.bread_experts_group.ffi.windows.nativeChoosePixelFormat
import org.bread_experts_group.ffi.windows.nativeCreateWindowExW
import org.bread_experts_group.ffi.windows.nativeDefWindowProcW
import org.bread_experts_group.ffi.windows.nativeDispatchMessageW
import org.bread_experts_group.ffi.windows.nativeGetDCEx
import org.bread_experts_group.ffi.windows.nativeGetMessageW
import org.bread_experts_group.ffi.windows.nativeGetModuleHandleW
import org.bread_experts_group.ffi.windows.nativePostQuitMessage
import org.bread_experts_group.ffi.windows.nativeReleaseDC
import org.bread_experts_group.ffi.windows.nativeSendMessageW
import org.bread_experts_group.ffi.windows.nativeSetPixelFormat
import org.bread_experts_group.ffi.windows.nativeTranslateMessage
import org.bread_experts_group.ffi.windows.stringToPCWSTR
import org.bread_experts_group.ffi.windows.win32MSG
import org.bread_experts_group.ffi.windows.win32PIXELFORMATDESCRIPTOR
import org.bread_experts_group.ffi.windows.win32PIXELFORMATDESCRIPTORcColorBits
import org.bread_experts_group.ffi.windows.win32PIXELFORMATDESCRIPTORcDepthBits
import org.bread_experts_group.ffi.windows.win32PIXELFORMATDESCRIPTORdwFlags
import org.bread_experts_group.ffi.windows.win32PIXELFORMATDESCRIPTORiPixelType
import org.bread_experts_group.ffi.windows.win32PIXELFORMATDESCRIPTORnSize
import org.bread_experts_group.ffi.windows.win32PIXELFORMATDESCRIPTORnVersion
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.util.EnumSet

class WindowsGraphicsWindow(private val template: WindowsGraphicsWindowTemplate) : GraphicsWindow() {
	private val arena: Arena = Arena.ofShared()
	internal lateinit var hdc: MemorySegment
	internal lateinit var hWnd: MemorySegment

	override val features: Set<GraphicsWindowFeatureImplementation<*>> = setOf(
		WindowsGraphicsWindowNameFeature(this),
		WindowsGraphicsWindowOpenGLContextFeature(this)
	)

	val procedures: MutableMap<Int, (MemorySegment, Int, Long, Long) -> Int> = mutableMapOf(
		WindowsMessageTypes.WM_DESTROY.position.toInt() to { _, _, _, _ ->
			nativePostQuitMessage.invokeExact(0)
			0
		}
	)

	fun wndProc(hWnd: MemorySegment, message: Int, wParam: Long, lParam: Long): Int {
		val proc = procedures[message] ?: return nativeDefWindowProcW.invokeExact(hWnd, message, wParam, lParam) as Int
		return proc(hWnd, message, wParam, lParam)
	}

	override fun open() {
		Thread.ofPlatform().start {
			val localHandle = nativeGetModuleHandleW.invokeExact(MemorySegment.NULL) as MemorySegment
			hWnd = nativeCreateWindowExW.invokeExact(
				0x00000300,
				MemorySegment.ofAddress(template.classAtom.toLong() and 0xFFFF),
				stringToPCWSTR(arena, this.get(GraphicsWindowFeatures.WINDOW_NAME)!!.name),
				0x10CC0000,
				0, 0,
				300, 400,
				MemorySegment.NULL,
				MemorySegment.NULL,
				localHandle,
				MemorySegment.NULL
			) as MemorySegment
			if (hWnd == MemorySegment.NULL) decodeLastError(arena)
			Thread.currentThread().name = "$hWnd Message Management"
			template.windows[hWnd] = this::wndProc
			hdc = nativeGetDCEx.invokeExact(
				hWnd,
				MemorySegment.NULL,
				0x00000002
			) as MemorySegment
			if (hdc == MemorySegment.NULL) throw COMException("Failed to retrieve window device context.")
			// TODO: Allow feature customization of the below format
			val pixelFormatDescriptor = arena.allocate(win32PIXELFORMATDESCRIPTOR)
			win32PIXELFORMATDESCRIPTORnSize.set(pixelFormatDescriptor, pixelFormatDescriptor.byteSize().toShort())
			win32PIXELFORMATDESCRIPTORnVersion.set(pixelFormatDescriptor, 1.toShort())
			win32PIXELFORMATDESCRIPTORdwFlags.set(
				pixelFormatDescriptor,
				EnumSet.of(
					WindowsPixelFormatDescriptorFlags.PFD_DRAW_TO_WINDOW,
					WindowsPixelFormatDescriptorFlags.PFD_SUPPORT_OPENGL,
					WindowsPixelFormatDescriptorFlags.PFD_DOUBLEBUFFER
				).raw().toInt()
			)
			win32PIXELFORMATDESCRIPTORiPixelType.set(
				pixelFormatDescriptor,
				WindowsPixelTypes.PFD_TYPE_RGBA.position.toByte()
			)
			win32PIXELFORMATDESCRIPTORcColorBits.set(pixelFormatDescriptor, 24.toByte())
			win32PIXELFORMATDESCRIPTORcDepthBits.set(pixelFormatDescriptor, 32.toByte())
			val pixelFormat = nativeChoosePixelFormat.invokeExact(
				hdc,
				pixelFormatDescriptor
			) as Int
			if (pixelFormat == 0) decodeLastError(arena)
			val formatSet = nativeSetPixelFormat.invokeExact(
				hdc,
				pixelFormat,
				pixelFormatDescriptor
			) as Boolean
			if (!formatSet) decodeLastError(arena)
			for (feature in this.features) if (feature is PreInitializableClosable) feature.open()
			val message = arena.allocate(win32MSG)
			while (true) {
				val status = nativeGetMessageW.invokeExact(message, MemorySegment.NULL, 0, 0) as Byte
				when (status.toInt()) {
					-1 -> decodeLastError(arena)
					0 -> break
					else -> {
						nativeTranslateMessage.invokeExact(message) as Byte
						nativeDispatchMessageW.invokeExact(message) as Int
					}
				}
			}
		}
	}

	override fun close() {
		for (feature in this.features) if (feature is AutoCloseable) feature.close()
		nativeReleaseDC.invokeExact(hWnd, hdc) as Int
		arena.close()
	}

	internal fun sendMessage(message: WindowsMessageTypes, wParam: Long, lParam: Long): Int {
		return nativeSendMessageW.invokeExact(hWnd, message.position.toInt(), wParam, lParam) as Int
	}
}