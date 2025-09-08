package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getLookup
import java.lang.foreign.Arena
import java.lang.foreign.Linker
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

private val handleArena = Arena.ofAuto()
private val ole32Lookup: SymbolLookup = handleArena.getLookup("Ole32.dll")
private val linker: Linker = Linker.nativeLinker()

val nativeCoCreateGuid: MethodHandle = ole32Lookup.getDowncall(
	linker, "CoCreateGuid", ValueLayout.JAVA_INT,
	ValueLayout.ADDRESS
)