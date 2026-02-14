package org.bread_experts_group.ffi.windows

import org.bread_experts_group.generic.Mappable.Companion.id
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.exception.IOAbortedException
import org.bread_experts_group.ffi.globalArena
import org.bread_experts_group.ffi.threadLocalPTR
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle


val INVALID_HANDLE_VALUE: MemorySegment = MemorySegment.ofAddress(0L - 1)

private val tlsDWORD0 = ThreadLocal.withInitial {
	globalArena.allocate(DWORD)
}
private val tlsDWORD1 = ThreadLocal.withInitial {
	globalArena.allocate(DWORD)
}
private val tlsDWORD2 = ThreadLocal.withInitial {
	globalArena.allocate(DWORD)
}
private val tlsULONG_PTR0 = ThreadLocal.withInitial {
	globalArena.allocate(ULONG_PTR)
}
private val tlsLARGE_INTEGER0 = ThreadLocal.withInitial {
	globalArena.allocate(LARGE_INTEGER)
}
val threadLocalDWORD0: MemorySegment
	get() = tlsDWORD0.get()
val threadLocalDWORD1: MemorySegment
	get() = tlsDWORD1.get()
val threadLocalDWORD2: MemorySegment
	get() = tlsDWORD2.get()
val threadLocalULONG_PTR0: MemorySegment
	get() = tlsULONG_PTR0.get()
val threadLocalLARGE_INTEGER0: MemorySegment
	get() = tlsLARGE_INTEGER0.get()

fun getWin32Error(err: Int): Throwable? = when (err) {
	995 -> IOAbortedException()
	0 -> null
	else -> {
		val count = nativeFormatMessageWide!!.invokeExact(
			0x00001100, // FORMAT_MESSAGE_ALLOCATE_BUFFER, FORMAT_MESSAGE_FROM_SYSTEM
			MemorySegment.NULL,
			err,
			0,
			threadLocalPTR,
			0,
			MemorySegment.NULL
		) as Int
		if (count == 0) TODO("Formatting error on error code: $err")
		val asString = threadLocalPTR
			.get(ValueLayout.ADDRESS, 0)
			.reinterpret(count.toLong() * 2)
			.toArray(ValueLayout.JAVA_BYTE)
			.toString(winCharsetWide)
			.trim()
		val deallocated = nativeLocalFree!!.invokeExact(
			threadLocalPTR.get(ValueLayout.ADDRESS, 0)
		) as MemorySegment
		if (deallocated != MemorySegment.NULL) TODO("LOCAL FREE GET LAST ERROR")
		WindowsLastErrorException(err.toUInt(), asString)
	}
}

fun tryThrowWin32Error(err: Int) {
	throw getWin32Error(err) ?: return
}

val win32LastError: Int
	get() = nativeGetLastError.get(capturedStateSegment, 0L) as Int
val wsaLastError: Int
	get() = nativeWSAGetLastError.get(capturedStateSegment, 0L) as Int

fun throwLastError(): Nothing = throw getWin32Error(win32LastError) ?: IllegalStateException()
fun throwLastWSAError(): Nothing = throw getWin32Error(wsaLastError) ?: IllegalStateException()

fun MethodHandle.returnsNTSTATUS(p0: Any) {
	(this.invoke(p0) as Int).decodeNTSTATUS()
}

fun MethodHandle.returnsNTSTATUS(p0: Any, p1: Any) {
	(this.invoke(p0, p1) as Int).decodeNTSTATUS()
}

fun MethodHandle.returnsNTSTATUS(p0: Any, p1: Any, p2: Any, p3: Any) {
	(this.invoke(p0, p1, p2, p3) as Int).decodeNTSTATUS()
}

fun MethodHandle.returnsNTSTATUS(p0: Any, p1: Any, p2: Any, p3: Any, p4: Any) {
	(this.invoke(p0, p1, p2, p3, p4) as Int).decodeNTSTATUS()
}

fun MethodHandle.returnsNTSTATUS(p0: Any, p1: Any, p2: Any, p3: Any, p4: Any, p5: Any) {
	(this.invoke(p0, p1, p2, p3, p4, p5) as Int).decodeNTSTATUS()
}

fun MethodHandle.returnsNTSTATUS(p0: Any, p1: Any, p2: Any, p3: Any, p4: Any, p5: Any, p6: Any) {
	(this.invoke(p0, p1, p2, p3, p4, p5, p6) as Int).decodeNTSTATUS()
}

fun MethodHandle.returnsNTSTATUS(p0: Any, p1: Any, p2: Any, p3: Any, p4: Any, p5: Any, p6: Any, p7: Any) {
	(this.invoke(p0, p1, p2, p3, p4, p5, p6, p7) as Int).decodeNTSTATUS()
}

private fun Int.decodeNTSTATUS() {
	val status = WindowsNTStatus.entries.id(this.toUInt())
	if (status.enum != WindowsNTStatus.STATUS_SUCCESS) throw WindowsNTSTATUSException(status)
}

@Suppress("FunctionName")
fun FILETIMEToUnixMs(fileTime: Long): Long = (fileTime / 10000) - 11644473600000