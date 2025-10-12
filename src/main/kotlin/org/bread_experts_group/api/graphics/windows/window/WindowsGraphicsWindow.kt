package org.bread_experts_group.api.graphics.windows.window

import org.bread_experts_group.Flaggable.Companion.raw
import org.bread_experts_group.api.PreInitializableClosable
import org.bread_experts_group.api.graphics.feature.window.GraphicsWindow
import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowFeatures
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.util.*
import java.util.concurrent.CountDownLatch

class WindowsGraphicsWindow(private val template: WindowsGraphicsWindowTemplate) : GraphicsWindow() {
	private val arena: Arena = Arena.ofShared()
	internal lateinit var hdc: MemorySegment
	internal lateinit var hWnd: MemorySegment

	val renderFeature = WindowsGraphicsWindowRenderEventFeature()
	val resizeFeature = WindowsGraphicsWindowResizeEventFeature()

	init {
		this.features += WindowsGraphicsWindowNameFeature(this)
		this.features += WindowsGraphicsWindowOpenGLContextFeature(this)
		this.features += WindowsGraphicsWindowDirectXContext(this)
		this.features += renderFeature
		this.features += resizeFeature
	}

	val procedures: MutableMap<Int, (MemorySegment, Int, Long, Long) -> Long> = mutableMapOf(
		WindowsMessageTypes.WM_DESTROY.position.toInt() to { _, _, _, _ ->
			nativePostQuitMessage!!.invokeExact(0)
			0
		},
		WindowsMessageTypes.WM_PAINT.position.toInt() to { _, _, _, _ ->
			renderFeature.lambda()
			0
		},
		WindowsMessageTypes.WM_SIZE.position.toInt() to { _, _, _, clientSize ->
			resizeFeature.lambda(
				clientSize and 0xFFFF,
				(clientSize shr 16) and 0xFFFF
			)
			0
		}
	)

	fun wndProc(hWnd: MemorySegment, message: Int, wParam: Long, lParam: Long): Long {
		val proc = procedures[message]
			?: return nativeDefWindowProcW!!.invokeExact(hWnd, message, wParam, lParam) as Long
		return proc(hWnd, message, wParam, lParam)
	}

	override fun open() {
		val initLatch = CountDownLatch(1)
		processingLock.acquire()
		Thread.ofPlatform().daemon().start {
			val localHandle = nativeGetModuleHandleW!!.invokeExact(MemorySegment.NULL) as MemorySegment
			hWnd = nativeCreateWindowExW!!.invokeExact(
				capturedStateSegment,
				0x00000300,
				MemorySegment.ofAddress(template.classAtom.toLong() and 0xFFFF),
				arena.allocateFrom(this.get(GraphicsWindowFeatures.WINDOW_NAME)!!.name, Charsets.UTF_16LE),
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
			hdc = nativeGetDCEx!!.invokeExact(
				hWnd,
				MemorySegment.NULL,
				0x00000002
			) as MemorySegment
			if (hdc == MemorySegment.NULL) throw COMException("Failed to retrieve window device context.")
			// TODO: Allow feature customization of the below format
			val pixelFormatDescriptor = arena.allocate(PIXELFORMATDESCRIPTOR)
			PIXELFORMATDESCRIPTOR_nSize.set(pixelFormatDescriptor, 0, pixelFormatDescriptor.byteSize().toShort())
			PIXELFORMATDESCRIPTOR_nVersion.set(pixelFormatDescriptor, 0, 1.toShort())
			PIXELFORMATDESCRIPTOR_dwFlags.set(
				pixelFormatDescriptor,
				0,
				EnumSet.of(
					WindowsPixelFormatDescriptorFlags.PFD_DRAW_TO_WINDOW,
					WindowsPixelFormatDescriptorFlags.PFD_SUPPORT_OPENGL,
					WindowsPixelFormatDescriptorFlags.PFD_DOUBLEBUFFER
				).raw().toInt()
			)
			PIXELFORMATDESCRIPTOR_iPixelType.set(
				pixelFormatDescriptor,
				0,
				WindowsPixelTypes.PFD_TYPE_RGBA.position.toByte()
			)
			PIXELFORMATDESCRIPTOR_cColorBits.set(pixelFormatDescriptor, 0, 24.toByte())
			PIXELFORMATDESCRIPTOR_cDepthBits.set(pixelFormatDescriptor, 0, 32.toByte())
			val pixelFormat = nativeChoosePixelFormat!!.invokeExact(
				capturedStateSegment,
				hdc,
				pixelFormatDescriptor
			) as Int
			if (pixelFormat == 0) decodeLastError(arena)
			val formatSet = nativeSetPixelFormat!!.invokeExact(
				capturedStateSegment,
				hdc,
				pixelFormat,
				pixelFormatDescriptor
			) as Int
			if (formatSet == 0) decodeLastError(arena)
			for (feature in this.features) if (feature is PreInitializableClosable) feature.open()
			initLatch.countDown()
			val message = arena.allocate(MSG)
			while (true) {
				val status = nativeGetMessageW!!.invokeExact(
					capturedStateSegment,
					message, MemorySegment.NULL, 0, 0
				) as Int
				when (status) {
					-1 -> decodeLastError(arena)
					0 -> break
					else -> {
						nativeTranslateMessage!!.invokeExact(message) as Int
						nativeDispatchMessageW!!.invokeExact(message) as Long
					}
				}
			}
			processingLock.release()
		}
		initLatch.await()
	}

	override fun close() {
		for (feature in this.features) if (feature is AutoCloseable) feature.close()
		nativeReleaseDC!!.invokeExact(hWnd, hdc) as Int
		arena.close()
	}

	internal fun sendMessage(message: WindowsMessageTypes, wParam: Long, lParam: Long): Long {
		return nativeSendMessageW!!.invokeExact(hWnd, message.position.toInt(), wParam, lParam) as Long
	}
}