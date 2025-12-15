package org.bread_experts_group.ffi.windows

import java.lang.invoke.MethodHandle
import java.nio.ByteOrder

val winCharsetWide = if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) Charsets.UTF_16LE else Charsets.UTF_16BE

val winCharset = if (UNICODE) {
	val codePage = nativeGetACP!!.invokeExact() as Int
	when (codePage) {
		65001 -> Charsets.UTF_8
		else -> winCharsetWide
	}
} else Charsets.US_ASCII

@Suppress("UNCHECKED_CAST")
fun <P, R, F : Function1<P, R>> codingSpecific(
	ansi: MethodHandle?,
	wide: MethodHandle?,
	action: (MethodHandle, P) -> R
): F? = when (winCharset) {
	winCharsetWide -> if (wide != null) { p: P -> action(wide, p) } else null
	else -> if (ansi != null) { p: P -> action(ansi, p) } else null
} as F?