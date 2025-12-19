package org.bread_experts_group.ffi.posix.x64

import org.bread_experts_group.ffi.capturedStateLayout
import org.bread_experts_group.ffi.capturedStateSegment
import java.lang.foreign.Linker
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.invoke.VarHandle

val ernCapture: Linker.Option? = try {
	Linker.Option.captureCallState("errno")
} catch (_: IllegalArgumentException) {
	null
}
val nativeErrno: VarHandle by lazy {
	capturedStateLayout.varHandle(groupElement("errno"))
}

val errno: Int
	get() = nativeErrno.get(capturedStateSegment, 0L) as Int

fun throwLastErrno(): Nothing {
	TODO("... $errno")
}