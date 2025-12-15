package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.*
import java.lang.foreign.MemorySegment
import java.lang.foreign.SymbolLookup
import java.lang.invoke.MethodHandle

private val shlwApiLookup: SymbolLookup? = globalArena.getLookup("Shlwapi.dll")

private val nativePathFindFileNameWide: MethodHandle? = shlwApiLookup.getDowncall(
	nativeLinker, "PathFindFileNameW",
	LPCWSTR,
	LPCWSTR.withName("pczPath")
)

private val nativePathFindFileNameANSI: MethodHandle? = shlwApiLookup.getDowncall(
	nativeLinker, "PathFindFileNameA",
	LPCSTR,
	LPCSTR.withName("pczPath")
)

val nativePathFindFileName = codingSpecific(
	nativePathFindFileNameWide,
	nativePathFindFileNameANSI
) { handle, parameters: String ->
	handle.invokeExact(
		capturedStateSegment,
		parameters
	) as MemorySegment
}