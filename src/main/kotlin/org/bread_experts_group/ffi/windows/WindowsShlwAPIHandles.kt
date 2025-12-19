package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getLookup
import org.bread_experts_group.ffi.globalArena
import org.bread_experts_group.ffi.nativeLinker
import java.lang.foreign.SymbolLookup
import java.lang.invoke.MethodHandle

private val shlwApiLookup: SymbolLookup? = globalArena.getLookup("Shlwapi.dll")

val nativePathFindFileNameWide: MethodHandle? = shlwApiLookup.getDowncall(
	nativeLinker, "PathFindFileNameW",
	LPCWSTR,
	LPCWSTR.withName("pczPath")
)