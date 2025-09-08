package org.bread_experts_group.api.graphics.windows.window

import org.bread_experts_group.api.FeatureImplementationSource
import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowOpenGLContextFeature
import org.bread_experts_group.api.graphics.feature.window.feature.opengl.OpenGLClearFlags
import org.bread_experts_group.coder.Flaggable.Companion.raw
import org.bread_experts_group.ffi.getDowncallVoid
import org.bread_experts_group.ffi.windows.WindowsMessageTypes
import org.bread_experts_group.ffi.windows.decodeLastError
import org.bread_experts_group.ffi.windows.nativeGetLastError
import org.bread_experts_group.ffi.windows.nativeGetProcAddress
import org.bread_experts_group.ffi.windows.nativeLoadLibraryExW
import org.bread_experts_group.ffi.windows.nativeSwapBuffers
import org.bread_experts_group.ffi.windows.nativeWGLCreateContext
import org.bread_experts_group.ffi.windows.nativeWGLDeleteContext
import org.bread_experts_group.ffi.windows.nativeWGLGetProcAddress
import org.bread_experts_group.ffi.windows.nativeWGLMakeCurrent
import org.bread_experts_group.ffi.windows.stringToPCSTR
import org.bread_experts_group.ffi.windows.stringToPCWSTR
import java.lang.foreign.Arena
import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

class WindowsGraphicsWindowOpenGLContextFeature(
	private val window: WindowsGraphicsWindow
) : GraphicsWindowOpenGLContextFeature() {
	private val arena = Arena.ofShared()
	override val source: FeatureImplementationSource = FeatureImplementationSource.SYSTEM_NATIVE
	private lateinit var hglrc: MemorySegment
	private val procedures: MutableMap<String, MethodHandle> = mutableMapOf()

	private val linker = Linker.nativeLinker()
	private val oglM = (nativeLoadLibraryExW.invokeExact(
		stringToPCWSTR(arena, "Opengl32.dll"),
		MemorySegment.NULL,
		0
	) as MemorySegment).also {
		if (it == MemorySegment.NULL) decodeLastError(arena)
	}

	fun procedureAddress(name: String): MemorySegment {
		val address = nativeWGLGetProcAddress.invokeExact(stringToPCSTR(arena, name)) as MemorySegment
		if (address == MemorySegment.NULL) {
			if (nativeGetLastError.invokeExact() as Int != 0x7F) decodeLastError(arena)
			val oglAddress = nativeGetProcAddress.invokeExact(oglM, stringToPCSTR(arena, name)) as MemorySegment
			if (oglAddress == MemorySegment.NULL) decodeLastError(arena)
			return oglAddress
		}
		return address
	}

	fun getHandleVoid(name: String, vararg layouts: ValueLayout) = procedures.getOrPut(name) {
		val addr = procedureAddress(name)
		addr.getDowncallVoid(linker, *layouts)
	}

	fun glClearColor(r: Float, g: Float, b: Float, a: Float) {
		getHandleVoid(
			"glClearColor",
			ValueLayout.JAVA_FLOAT, ValueLayout.JAVA_FLOAT, ValueLayout.JAVA_FLOAT, ValueLayout.JAVA_FLOAT
		).invokeExact(r, g, b, a)
	}

	fun glClear(vararg flags: OpenGLClearFlags) {
		getHandleVoid(
			"glClear",
			ValueLayout.JAVA_INT
		).invokeExact(flags.raw().toInt())
	}

	override fun open() {
		if (!use) return
		hglrc = nativeWGLCreateContext.invokeExact(window.hdc) as MemorySegment
		if (hglrc == MemorySegment.NULL) decodeLastError(arena)
		val makeStatus = nativeWGLMakeCurrent.invokeExact(window.hdc, hglrc) as Boolean
		if (!makeStatus) decodeLastError(arena)
		window.procedures[WindowsMessageTypes.WM_PAINT.position.toInt()] = { _, _, _, _ ->
			glClearColor(0f, 0f, 0f, 1f)
			glClear(OpenGLClearFlags.GL_COLOR_BUFFER_BIT, OpenGLClearFlags.GL_DEPTH_BUFFER_BIT)
			nativeSwapBuffers.invokeExact(window.hdc) as Boolean
			0
		}
	}

	override fun close() {
		nativeWGLMakeCurrent.invokeExact(MemorySegment.NULL, MemorySegment.NULL) as Boolean
		nativeWGLDeleteContext.invokeExact(hglrc) as Boolean
		arena.close()
	}
}