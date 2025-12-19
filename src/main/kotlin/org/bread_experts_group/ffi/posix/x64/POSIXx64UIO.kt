@file:Suppress("DANGEROUS_CHARACTERS")

package org.bread_experts_group.ffi.posix.x64

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.nativeLinker
import java.lang.foreign.AddressLayout
import java.lang.foreign.SymbolLookup
import java.lang.invoke.MethodHandle

fun posix64ReadV(
	lookup: SymbolLookup?,
	`iovec*`: AddressLayout
): MethodHandle? = lookup?.getDowncall(
	nativeLinker, "readv",
	arrayOf(
		ssize_t,
		int.withName("fildes"),
		`iovec*`.withName("iov"),
		int.withName("iovcnt")
	),
	listOf(ernCapture)
)

fun posix64WriteV(
	lookup: SymbolLookup?,
	`iovec*`: AddressLayout
): MethodHandle? = lookup?.getDowncall(
	nativeLinker, "writev",
	arrayOf(
		ssize_t,
		int.withName("fildes"),
		`iovec*`.withName("iov"),
		int.withName("iovcnt")
	),
	listOf(ernCapture)
)