package org.bread_experts_group.ffi.windows

import org.bread_experts_group.Mappable.Companion.id
import org.bread_experts_group.ffi.OperatingSystemException
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.globalArena
import java.lang.foreign.AddressLayout
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

val char: ValueLayout.OfByte = ValueLayout.JAVA_BYTE
val BYTE: ValueLayout.OfByte = ValueLayout.JAVA_BYTE
val WORD: ValueLayout.OfShort = ValueLayout.JAVA_SHORT
val ATOM = WORD
val DWORD: ValueLayout.OfInt = ValueLayout.JAVA_INT
val PDWORD: AddressLayout = ValueLayout.ADDRESS
val BOOL: ValueLayout.OfInt = ValueLayout.JAVA_INT
val LONG: ValueLayout.OfInt = ValueLayout.JAVA_INT
val ULONG: ValueLayout.OfInt = ValueLayout.JAVA_INT
val ULONG_PTR: AddressLayout = ValueLayout.ADDRESS
val PULONG: AddressLayout = ValueLayout.ADDRESS
val PBYTE: AddressLayout = ValueLayout.ADDRESS
val ULONGLONG: ValueLayout.OfLong = ValueLayout.JAVA_LONG
val USHORT: ValueLayout.OfShort = ValueLayout.JAVA_SHORT
val HRESULT = LONG
val LONG_PTR: ValueLayout.OfLong = ValueLayout.JAVA_LONG
val UINT_PTR: ValueLayout.OfLong = ValueLayout.JAVA_LONG
val LRESULT = LONG_PTR
val UINT: ValueLayout.OfInt = ValueLayout.JAVA_INT
val SHORT: ValueLayout.OfShort = ValueLayout.JAVA_SHORT
val WCHAR: ValueLayout.OfShort = ValueLayout.JAVA_SHORT
val CHAR: ValueLayout.OfByte = ValueLayout.JAVA_BYTE
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
val PHANDLE: AddressLayout = AddressLayout.ADDRESS
val ACCESS_MASK = DWORD

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
private val tlsPTR = ThreadLocal.withInitial {
	globalArena.allocate(ValueLayout.ADDRESS)
}
val threadLocalDWORD0: MemorySegment
	get() = tlsDWORD0.get()
val threadLocalDWORD1: MemorySegment
	get() = tlsDWORD1.get()
val threadLocalDWORD2: MemorySegment
	get() = tlsDWORD2.get()
val threadLocalPTR: MemorySegment
	get() = tlsPTR.get()

fun decodeCOMError(err: Int) {
	if (err != 0) {
		val count = nativeFormatMessageW!!.invokeExact(
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
			.toString(Charsets.UTF_16LE)
			.trim()
		val deallocated = nativeLocalFree!!.invokeExact(
			threadLocalPTR.get(ValueLayout.ADDRESS, 0)
		) as MemorySegment
		if (deallocated != MemorySegment.NULL) TODO("LOCAL FREE GET LAST ERROR")
		throw WindowsLastErrorException(err.toUInt(), asString)
	}
}

fun decodeLastError(): Nothing {
	decodeCOMError(nativeGetLastError.get(capturedStateSegment, 0L) as Int)
	throw OperatingSystemException("General exception (GetLastError did not produce error code).")
}

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