package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.COMException
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

fun decodeCOMError(arena: Arena, err: Int) {
	if (err != 0) {
		val bufferPointer = arena.allocate(ValueLayout.ADDRESS)
		val count = nativeFormatMessageW.invokeExact(
			0x00001100, // FORMAT_MESSAGE_ALLOCATE_BUFFER, FORMAT_MESSAGE_FROM_SYSTEM
			MemorySegment.NULL,
			err,
			0,
			bufferPointer,
			0,
			MemorySegment.NULL
		) as Int
		if (count == 0) TODO("FORMAT MESSAGE GET LAST ERROR")
		val asString = bufferPointer
			.get(ValueLayout.ADDRESS, 0)
			.reinterpret(count.toLong() * 2)
			.toArray(ValueLayout.JAVA_BYTE)
			.toString(Charsets.UTF_16LE)
			.trim()
		val deallocated = nativeLocalFree.invokeExact(
			bufferPointer.get(ValueLayout.ADDRESS, 0)
		) as MemorySegment
		if (deallocated != MemorySegment.NULL) TODO("LOCAL FREE GET LAST ERROR")
		throw COMException("0x${err.toHexString(HexFormat.UpperCase)}: \"$asString\"")
	}
}

fun decodeLastError(arena: Arena) = decodeCOMError(arena, nativeGetLastError.invokeExact() as Int)

fun stringToPCWSTR(arena: Arena, string: String): MemorySegment {
	val encoded = string.toByteArray(Charsets.UTF_16LE)
	val pathAllocated = arena.allocate(encoded.size + 2L)
	encoded.forEachIndexed { i, b -> pathAllocated.set(ValueLayout.JAVA_BYTE, i.toLong(), b) }
	return pathAllocated
}

fun wPCWSTRToString(from: MemorySegment): String {
	var filePath = ""
	var offset = 0L
	while (offset < from.byteSize()) {
		val next = from.get(ValueLayout.JAVA_SHORT, offset).toUShort()
		if (next == UShort.MIN_VALUE) break
		offset += 2
		filePath += Char(next)
	}
	return filePath
}

fun segmentToGUID(s: MemorySegment, offset: Long): WindowsGUID = WindowsGUID(
	s.get(ValueLayout.JAVA_INT, offset).toUInt(),
	s.get(ValueLayout.JAVA_SHORT, offset + 2).toUShort(),
	s.get(ValueLayout.JAVA_SHORT, offset + 4).toUShort(),
	s.asSlice(offset + 6, 2).toArray(ValueLayout.JAVA_BYTE),
	s.asSlice(offset + 8, 6).toArray(ValueLayout.JAVA_BYTE),
)

fun win32ToHResult(x: Int): Int = if (x <= 0) x else ((x and 0x0000FFFF) or (8 shl 16) or 0x80000000.toInt())