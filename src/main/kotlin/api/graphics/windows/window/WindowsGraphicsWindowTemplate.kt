package org.bread_experts_group.api.graphics.windows.window

import org.bread_experts_group.api.graphics.feature.window.GraphicsWindowTemplate
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

	@Suppress("unused")
	fun wndProc(hWnd: MemorySegment, message: Int, wParam: Long, lParam: Long): Int {
		return nativeDefWindowProcW.invokeExact(hWnd, message, wParam, lParam) as Int
	}

	init {
		val classExA = arena.allocate(win32WNDCLASSEXA)
		val localHandle = nativeGetModuleHandleW.invokeExact(MemorySegment.NULL) as MemorySegment
		val methodHandles = MethodHandles.lookup()
		win32WNDCLASSEXAcbSize.set(classExA, classExA.byteSize().toInt())
		win32WNDCLASSEXAlpfnWndProc.set(
			classExA,
			linker.upcallStub(
				methodHandles.findSpecial(
					this::class.java, "wndProc",
					MethodType.methodType(
						Int::class.java,
						MemorySegment::class.java, Int::class.java, Long::class.java, Long::class.java
					), this::class.java
				).bindTo(this),
				FunctionDescriptor.of(
					ValueLayout.JAVA_INT,
					ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_LONG, ValueLayout.JAVA_LONG
				),
				arena
			)
		)
		win32WNDCLASSEXAhInstance.set(classExA, localHandle)
		win32WNDCLASSEXAlpszClassName.set(classExA, stringToPCWSTR(arena, "bsl${counter++}"))
		// TODO: Needs extensibility for hIcon/hCursor/hbrBackground/menuName/hIconSm/style
		val classAtom = nativeRegisterClassExW.invokeExact(classExA) as Short
		if (classAtom == 0.toShort()) decodeLastError(arena)
		this.classAtom = classAtom
	}
}