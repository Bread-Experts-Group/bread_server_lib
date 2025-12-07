package org.bread_experts_group.ffi.posix

import org.bread_experts_group.ffi.OperatingSystemException
import org.bread_experts_group.ffi.capturedStateSegment
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

var errno: Int
	get() = nativeErrno.get(capturedStateSegment, 0L) as Int
	set(value) = nativeErrno.set(capturedStateSegment, 0L, value)

fun decodeErrno(decodeErrno: Int) {
	if (decodeErrno != 0) Arena.ofConfined().use { tempArena ->
		val locale = nativeNewLocale!!.invokeExact(
			capturedStateSegment,
			0,
			tempArena.allocateFrom("", Charsets.UTF_8),
			MemorySegment.NULL
		) as MemorySegment
		if (locale == MemorySegment.NULL) TODO("ERRNO LOCALE ERROR $errno")
		locale.reinterpret(tempArena) { nativeFreeLocale!!.invokeExact(it) }
		val nameSeg = nativeStrErrorNameNP!!.invokeExact(
			decodeErrno
		) as MemorySegment
		val name = nameSeg.reinterpret(Long.MAX_VALUE).getString(0, Charsets.UTF_8)
		val descriptionSeg = nativeStrErrorL!!.invokeExact(
			decodeErrno,
			locale
		) as MemorySegment
		val description = descriptionSeg.reinterpret(Long.MAX_VALUE).getString(0, Charsets.UTF_8)
		throw POSIXErrnoException(errno.toUInt(), name, description)
	}
}

fun throwLastErrno(): Nothing {
	decodeErrno(errno)
	throw OperatingSystemException("General exception (errno did not produce error code).")
}