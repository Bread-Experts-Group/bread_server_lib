package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getLookup
import java.lang.foreign.Arena
import java.lang.foreign.Linker
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

private val handleArena = Arena.ofAuto()
private val kernel32Lookup: SymbolLookup = handleArena.getLookup("Kernel32.dll")
private val linker: Linker = Linker.nativeLinker()

val nativeFormatMessageW: MethodHandle = kernel32Lookup.getDowncall(
	linker, "FormatMessageW", ValueLayout.JAVA_INT,
	ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT,
	ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT,
	ValueLayout.ADDRESS
)

val nativeLocalFree: MethodHandle = kernel32Lookup.getDowncall(
	linker, "LocalFree", ValueLayout.ADDRESS,
	ValueLayout.ADDRESS
)

val nativeGetModuleHandleW: MethodHandle = kernel32Lookup.getDowncall(
	linker, "GetModuleHandleW", ValueLayout.ADDRESS,
	ValueLayout.ADDRESS
)

val nativeGetLastError: MethodHandle = kernel32Lookup.getDowncall(
	linker, "GetLastError", ValueLayout.JAVA_INT
)

val nativeLoadLibraryExW: MethodHandle = kernel32Lookup.getDowncall(
	linker, "LoadLibraryExW", ValueLayout.ADDRESS,
	ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT
)

val nativeGetProcAddress: MethodHandle = kernel32Lookup.getDowncall(
	linker, "GetProcAddress", ValueLayout.ADDRESS,
	ValueLayout.ADDRESS, ValueLayout.ADDRESS
)