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
val ULONG: ValueLayout.OfInt = ValueLayout.JAVA_INT
val HRESULT = LONG
val LONG_PTR: ValueLayout.OfLong = ValueLayout.JAVA_LONG
val UINT_PTR: ValueLayout.OfLong = ValueLayout.JAVA_LONG
val LRESULT = LONG_PTR
val UINT: ValueLayout.OfInt = ValueLayout.JAVA_INT
val WPARAM = UINT_PTR
val LPARAM = LONG_PTR
val PWSTR: AddressLayout = ValueLayout.ADDRESS
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

fun decodeCOMError(arena: Arena, err: Int) {
	if (err != 0) {
		val bufferPointer = arena.allocate(ValueLayout.ADDRESS)
		val count = nativeFormatMessageW!!.invokeExact(
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
		val deallocated = nativeLocalFree!!.invokeExact(
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