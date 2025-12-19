@file:Suppress("LocalVariableName", "DANGEROUS_CHARACTERS")

package org.bread_experts_group.ffi.posix.x64

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.nativeLinker
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

fun posix64GetCwd(lookup: SymbolLookup?): MethodHandle? = lookup?.getDowncall(
	nativeLinker, "getcwd",
	arrayOf(
		`char*`,
		`char*`.withName("buf"),
		size_t.withName("size")
	),
	listOf(ernCapture)
)

fun posix64LSeek(lookup: SymbolLookup?, off_t: ValueLayout): MethodHandle? = lookup?.getDowncall(
	nativeLinker, "lseek",
	arrayOf(
		off_t,
		int.withName("fildes"),
		off_t.withName("offset"),
		int.withName("whence")
	),
	listOf(ernCapture)
)

fun posix64FTruncate(lookup: SymbolLookup?, off_t: ValueLayout): MethodHandle? = lookup?.getDowncall(
	nativeLinker, "ftruncate",
	arrayOf(
		int,
		int.withName("fildes"),
		off_t.withName("length")
	),
	listOf(ernCapture)
)

fun posix64FStat(lookup: SymbolLookup?, `stat*`: ValueLayout): MethodHandle? = lookup?.getDowncall(
	nativeLinker, "fstat",
	arrayOf(
		int,
		int.withName("fildes"),
		`stat*`.withName("buf")
	),
	listOf(ernCapture)
)