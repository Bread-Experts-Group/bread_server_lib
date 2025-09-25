package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.capturedStateSegment
import java.lang.foreign.AddressLayout
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

val char: ValueLayout.OfByte = ValueLayout.JAVA_BYTE
val BYTE: ValueLayout.OfByte = ValueLayout.JAVA_BYTE
val WORD: ValueLayout.OfShort = ValueLayout.JAVA_SHORT
val ATOM = WORD
val DWORD: ValueLayout.OfInt = ValueLayout.JAVA_INT
val BOOL: ValueLayout.OfInt = ValueLayout.JAVA_INT
val LONG: ValueLayout.OfInt = ValueLayout.JAVA_INT
val HRESULT = LONG
val LONG_PTR: ValueLayout.OfLong = ValueLayout.JAVA_LONG
val UINT_PTR: ValueLayout.OfLong = ValueLayout.JAVA_LONG
val LRESULT = LONG_PTR
val UINT: ValueLayout.OfInt = ValueLayout.JAVA_INT
val WPARAM = UINT_PTR
val LPARAM = LONG_PTR
val LPDWORD: AddressLayout = ValueLayout.ADDRESS
val LPWSTR: AddressLayout = ValueLayout.ADDRESS
val LPCWSTR: AddressLayout = ValueLayout.ADDRESS
val LPCSTR: AddressLayout = ValueLayout.ADDRESS
val LPCVOID: AddressLayout = ValueLayout.ADDRESS
val LPVOID: AddressLayout = AddressLayout.ADDRESS
val PVOID: AddressLayout = AddressLayout.ADDRESS
val HANDLE = PVOID
val HLOCAL = HANDLE
val HMENU = HANDLE
val HINSTANCE = HANDLE
val HRGN = HANDLE
val HWND = HANDLE
val HMODULE = HINSTANCE
val HWINSTA = HANDLE
val HDESK = HANDLE
val HICON = HANDLE
val HCURSOR = HICON
val HBRUSH = HANDLE
val FARPROC: AddressLayout = AddressLayout.ADDRESS
val PROC: AddressLayout = AddressLayout.ADDRESS
val ACCESS_MASK = DWORD

val INVALID_HANDLE_VALUE: MemorySegment = MemorySegment.ofAddress(0L - 1)

fun makeWord(highByte: UByte, lowByte: UByte): UShort = (highByte.toInt() shl 8 or lowByte.toInt()).toUShort()

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

fun decodeLastError(arena: Arena) {
	decodeCOMError(arena, nativeGetLastError.get(capturedStateSegment, 0L) as Int)
	throw COMException("General exception (GetLastError did not produce error code).")
}

fun stringToPCSTR(arena: Arena, string: String): MemorySegment {
	val encoded = string.toByteArray(Charsets.US_ASCII)
	val allocated = arena.allocate(encoded.size + 1L)
	encoded.forEachIndexed { i, b -> allocated.set(ValueLayout.JAVA_BYTE, i.toLong(), b) }
	return allocated
}

fun stringToPCWSTR(arena: Arena, string: String): MemorySegment {
	val encoded = string.toByteArray(Charsets.UTF_16LE)
	val allocated = arena.allocate(encoded.size + 2L)
	encoded.forEachIndexed { i, b -> allocated.set(ValueLayout.JAVA_BYTE, i.toLong(), b) }
	return allocated
}

@ExperimentalUnsignedTypes
fun segmentToGUID(s: MemorySegment, offset: Long): WindowsGUID = WindowsGUID(
	s.get(ValueLayout.JAVA_INT, offset).toUInt(),
	s.get(ValueLayout.JAVA_SHORT, offset + 2).toUShort(),
	s.get(ValueLayout.JAVA_SHORT, offset + 4).toUShort(),
	s.asSlice(offset + 6, 2).toArray(ValueLayout.JAVA_BYTE).toUByteArray(),
	s.asSlice(offset + 8, 6).toArray(ValueLayout.JAVA_BYTE).toUByteArray(),
)

fun win32ToHResult(x: Int): Int = if (x <= 0) x else ((x and 0x0000FFFF) or (8 shl 16) or 0x80000000.toInt())