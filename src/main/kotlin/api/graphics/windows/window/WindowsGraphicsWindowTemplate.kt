package org.bread_experts_group.api.graphics.windows.window

import org.bread_experts_group.api.graphics.feature.window.GraphicsWindowTemplate
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.*
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

class WindowsGraphicsWindowTemplate : GraphicsWindowTemplate() {
	companion object {
		var counter = 0
	}

	private val arena: Arena = Arena.ofConfined()
	private val linker: Linker = Linker.nativeLinker()
	val classAtom: Short
	val windows: MutableMap<MemorySegment, (MemorySegment, Int, Long, Long) -> Long> = mutableMapOf()

	@Suppress("unused")
	fun wndProc(hWnd: MemorySegment, message: Int, wParam: Long, lParam: Long): Long {
		val proc = windows[hWnd] ?: return nativeDefWindowProcW.invokeExact(hWnd, message, wParam, lParam) as Long
		return proc(hWnd, message, wParam, lParam)
	}

	init {
		val classExA = arena.allocate(WNDCLASSEXA)
		val localHandle = nativeGetModuleHandleW.invokeExact(MemorySegment.NULL) as MemorySegment
		val methodHandles = MethodHandles.lookup()
		WNDCLASSEXA_cbSize.set(classExA, 0, classExA.byteSize().toInt())
		WNDCLASSEXA_lpfnWndProc.set(
			classExA,
			0,
			linker.upcallStub(
				methodHandles.findSpecial(
					this::class.java, "wndProc",
					MethodType.methodType(
						Long::class.java,
						MemorySegment::class.java, Int::class.java, Long::class.java, Long::class.java
					), this::class.java
				).bindTo(this),
				FunctionDescriptor.of(
					ValueLayout.JAVA_LONG,
					ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_LONG, ValueLayout.JAVA_LONG
				),
				arena
			)
		)
		WNDCLASSEXA_hInstance.set(classExA, 0, localHandle)
		WNDCLASSEXA_lpszClassName.set(classExA, 0, stringToPCWSTR(arena, "bsl${counter++}"))
		// TODO: Needs extensibility for hIcon/hCursor/hbrBackground/menuName/hIconSm/style
		val classAtom = nativeRegisterClassExW.invokeExact(capturedStateSegment, classExA) as Short
		if (classAtom == 0.toShort()) decodeLastError(arena)
		this.classAtom = classAtom
	}
}