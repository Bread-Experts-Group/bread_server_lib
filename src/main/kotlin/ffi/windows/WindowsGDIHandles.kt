package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getLookup
import java.lang.foreign.Arena
import java.lang.foreign.Linker
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

private val handleArena = Arena.ofAuto()
private val gdi32Lookup: SymbolLookup = handleArena.getLookup("Gdi32.dll")
private val linker: Linker = Linker.nativeLinker()

val nativeSetPixelFormat: MethodHandle = gdi32Lookup.getDowncall(
	linker, "SetPixelFormat", ValueLayout.JAVA_BOOLEAN,
	ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS
)

val nativeChoosePixelFormat: MethodHandle = gdi32Lookup.getDowncall(
	linker, "ChoosePixelFormat", ValueLayout.JAVA_INT,
	ValueLayout.ADDRESS, ValueLayout.ADDRESS
)

val nativeSwapBuffers: MethodHandle = gdi32Lookup.getDowncall(
	linker, "SwapBuffers", ValueLayout.JAVA_BOOLEAN,
	ValueLayout.ADDRESS
)