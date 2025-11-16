package org.bread_experts_group.ffi.macos

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.nativeLinker
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

private val libcLookup: SymbolLookup? = nativeLinker.defaultLookup()

val nativeGetlogin: MethodHandle? = libcLookup.getDowncall(
	nativeLinker, "getlogin",
	ValueLayout.ADDRESS
)
