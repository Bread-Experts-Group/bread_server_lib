package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

private val advapi32Lookup: SymbolLookup? = globalArena.getLookup("Advapi32.dll")

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
	if (status == 0) throwLastError()
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

val nativeFileEncryptionStatusW: MethodHandle? = advapi32Lookup.getDowncall(
	nativeLinker, "FileEncryptionStatusW",
	arrayOf(
		BOOL,
		LPCWSTR, LPDWORD
	),
	listOf(
		gleCapture
	)
)

val nativeOpenEncryptedFileRawW: MethodHandle? = advapi32Lookup.getDowncall(
	nativeLinker, "OpenEncryptedFileRawW",
	arrayOf(
		DWORD,
		LPCWSTR, ULONG, PVOID
	),
	listOf()
)

val nativeReadEncryptedFileRaw: MethodHandle? = advapi32Lookup.getDowncall(
	nativeLinker, "ReadEncryptedFileRaw",
	arrayOf(
		DWORD,
		ValueLayout.ADDRESS, PVOID, PVOID
	),
	listOf()
)

val nativeEncryptFileW: MethodHandle? = advapi32Lookup.getDowncall(
	nativeLinker, "EncryptFileW",
	arrayOf(
		BOOL,
		LPCWSTR
	),
	listOf(
		gleCapture
	)
)

val nativeDecryptFileW: MethodHandle? = advapi32Lookup.getDowncall(
	nativeLinker, "DecryptFileW",
	arrayOf(
		BOOL,
		LPCWSTR, DWORD
	),
	listOf(
		gleCapture
	)
)

val nativeEncryptionDisable: MethodHandle? = advapi32Lookup.getDowncall(
	nativeLinker, "EncryptionDisable",
	arrayOf(
		BOOL,
		LPCWSTR, BOOL
	),
	listOf(
		gleCapture
	)
)

val nativeCloseEncryptedFileRaw: MethodHandle? = advapi32Lookup.getDowncallVoid(
	nativeLinker, "CloseEncryptedFileRaw",
	PVOID
)