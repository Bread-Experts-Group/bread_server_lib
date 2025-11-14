package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

private val advapi32Lookup: SymbolLookup? = globalArena.getLookup("Advapi32.dll")

val nativeGetUserNameW: MethodHandle? = advapi32Lookup.getDowncall(
	nativeLinker, "GetUserNameW",
	arrayOf(
		BOOL,
		LPWSTR, LPDWORD
	),
	listOf(
		gleCapture
	)
)

val nativeOpenThreadToken: MethodHandle? = advapi32Lookup.getDowncall(
	nativeLinker, "OpenThreadToken",
	arrayOf(
		BOOL,
		HANDLE, DWORD, BOOL, PHANDLE
	),
	listOf(
		gleCapture
	)
)

val nativeGetTokenInformation: MethodHandle? = advapi32Lookup.getDowncall(
	nativeLinker, "GetTokenInformation",
	arrayOf(
		BOOL,
		HANDLE, ValueLayout.JAVA_INT, LPVOID, DWORD, PDWORD
	),
	listOf(
		gleCapture
	)
)

fun getTokenInformation(arena: Arena, token: MemorySegment, infoClass: WindowsTokenInformationClass): MemorySegment {
	nativeGetTokenInformation!!.invokeExact(
		capturedStateSegment,
		token,
		infoClass.id.toInt(),
		MemorySegment.NULL,
		0,
		threadLocalDWORD0
	) as Int
	val data = arena.allocate(threadLocalDWORD0.get(DWORD, 0).toLong())
	val status = nativeGetTokenInformation.invokeExact(
		capturedStateSegment,
		token,
		infoClass.id.toInt(),
		data,
		data.byteSize().toInt(),
		threadLocalDWORD0
	) as Int
	if (status == 0) decodeLastError()
	return data
}

val nativeLookupAccountSidW: MethodHandle? = advapi32Lookup.getDowncall(
	nativeLinker, "LookupAccountSidW",
	arrayOf(
		BOOL,
		LPCWSTR, ValueLayout.ADDRESS, /* of SID */ LPWSTR, LPDWORD, LPWSTR, LPDWORD,
		ValueLayout.ADDRESS /* of SID_NAME_USE  */
	),
	listOf(
		gleCapture
	)
)