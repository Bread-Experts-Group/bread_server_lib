package org.bread_experts_group.ffi.posix.x64

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.nativeLinker
import java.lang.foreign.SymbolLookup
import java.lang.invoke.MethodHandle

fun posix64Socket(lookup: SymbolLookup?): MethodHandle? = lookup?.getDowncall(
	nativeLinker, "socket",
	arrayOf(
		int,
		int.withName("domain"),
		int.withName("type"),
		int.withName("protocol")
	),
	listOf(
		ernCapture
	)
)