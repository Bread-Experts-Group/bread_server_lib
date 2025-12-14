package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getLookup
import org.bread_experts_group.ffi.globalArena
import org.bread_experts_group.ffi.nativeLinker
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

private val gdi32Lookup: SymbolLookup? = globalArena.getLookup("Gdi32.dll")

val nativeSetPixelFormat: MethodHandle? = gdi32Lookup.getDowncall(
	nativeLinker, "SetPixelFormat",
	arrayOf(
		BOOL,
		HDC, ValueLayout.JAVA_INT, ValueLayout.ADDRESS
	),
	listOf(
		gleCapture
	)
)

val nativeChoosePixelFormat: MethodHandle? = gdi32Lookup.getDowncall(
	nativeLinker, "ChoosePixelFormat",
	arrayOf(
		ValueLayout.JAVA_INT,
		HDC, ValueLayout.ADDRESS
	),
	listOf(
		gleCapture
	)
)

val nativeSwapBuffers: MethodHandle? = gdi32Lookup.getDowncall(
	nativeLinker, "SwapBuffers", BOOL,
	HDC
)