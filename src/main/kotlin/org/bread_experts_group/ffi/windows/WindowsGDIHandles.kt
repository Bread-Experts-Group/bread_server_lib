package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getLookup
import java.lang.foreign.*
import java.lang.invoke.MethodHandle

val HDC: AddressLayout = ValueLayout.ADDRESS

private val handleArena = Arena.ofAuto()
private val gdi32Lookup: SymbolLookup? = handleArena.getLookup("Gdi32.dll")
private val linker: Linker = Linker.nativeLinker()

val nativeSetPixelFormat: MethodHandle? = gdi32Lookup.getDowncall(
	linker, "SetPixelFormat",
	arrayOf(
		BOOL,
		HDC, ValueLayout.JAVA_INT, ValueLayout.ADDRESS
	),
	listOf(
		gleCapture
	)
)

val nativeChoosePixelFormat: MethodHandle? = gdi32Lookup.getDowncall(
	linker, "ChoosePixelFormat",
	arrayOf(
		ValueLayout.JAVA_INT,
		HDC, ValueLayout.ADDRESS
	),
	listOf(
		gleCapture
	)
)

val nativeSwapBuffers: MethodHandle? = gdi32Lookup.getDowncall(
	linker, "SwapBuffers", BOOL,
	HDC
)