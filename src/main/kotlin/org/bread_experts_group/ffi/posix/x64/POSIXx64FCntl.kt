package org.bread_experts_group.ffi.posix.x64

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.nativeLinker
import java.lang.foreign.Linker
import java.lang.foreign.SymbolLookup
import java.lang.invoke.MethodHandle

@Suppress("FunctionName")
fun posix64Open_vInt(lookup: SymbolLookup?): MethodHandle? = lookup?.getDowncall(
	nativeLinker, "open",
	arrayOf(
		int,
		`char*`.withName("path"),
		int.withName("oflag"),
		int.withName("v_arg")
	),
	listOf(
		ernCapture,
		Linker.Option.firstVariadicArg(2)
	)
)