package org.bread_experts_group.ffi.macos

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getLookup
import org.bread_experts_group.ffi.globalArena
import org.bread_experts_group.ffi.nativeLinker
import java.lang.foreign.SymbolLookup
import java.lang.invoke.MethodHandle

private val macOSLibSystemLookup: SymbolLookup? = globalArena.getLookup("libSystem.B.dylib")

val nativeGetlogin: MethodHandle? = macOSLibSystemLookup.getDowncall(
	nativeLinker, "getlogin",
	MACOS_PTR
)

val nativeIsatty: MethodHandle? = macOSLibSystemLookup.getDowncall(
	nativeLinker, "isatty",
	MACOS_INT,
	MACOS_INT
)