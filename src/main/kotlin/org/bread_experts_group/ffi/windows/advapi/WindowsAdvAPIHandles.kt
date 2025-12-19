@file:Suppress("FunctionName")

package org.bread_experts_group.ffi.windows.advapi

import org.bread_experts_group.Mappable.Companion.id
import org.bread_experts_group.MappedEnumeration
import org.bread_experts_group.ffi.*
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

private val advapi32Lookup: SymbolLookup? = globalArena.getLookup("Advapi32.dll")

private val nativeGetTokenInformation: MethodHandle? = advapi32Lookup.getDowncall(
	nativeLinker, "GetTokenInformation",
	arrayOf(
		BOOL,
		HANDLE.withName("TokenHandle"),
		TOKEN_INFORMATION_CLASS.withName("TokenInformationClass"),
		LPVOID.withName("TokenInformation"),
		DWORD.withName("TokenInformationLength"),
		PDWORD.withName("ReturnLength")
	),
	listOf(
		gleCapture
	)
)

val getTokenInformation = if (nativeGetTokenInformation != null) { arena: Arena, token: MemorySegment,
																   infoClass: _TOKEN_INFORMATION_CLASS ->
	nativeGetTokenInformation.invokeExact(
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
	data
} else null

private val nativeLookupAccountSidWide: MethodHandle? = advapi32Lookup.getDowncall(
	nativeLinker, "LookupAccountSidW",
	arrayOf(
		BOOL,
		LPCWSTR.withName("lpSystemName"),
		PSID.withName("Sid"),
		LPWSTR.withName("Name"),
		LPDWORD.withName("cchName"),
		LPWSTR.withName("ReferencedDomainName"),
		LPDWORD.withName("cchReferencedDomainName"),
		PSID_NAME_USE.withName("peUse")
	),
	listOf(
		gleCapture
	)
)

private val nativeLookupAccountSidANSI: MethodHandle? = advapi32Lookup.getDowncall(
	nativeLinker, "LookupAccountSidA",
	arrayOf(
		BOOL,
		LPCSTR.withName("lpSystemName"),
		PSID.withName("Sid"),
		LPSTR.withName("Name"),
		LPDWORD.withName("cchName"),
		LPSTR.withName("ReferencedDomainName"),
		LPDWORD.withName("cchReferencedDomainName"),
		PSID_NAME_USE.withName("peUse")
	),
	listOf(
		gleCapture
	)
)

data class LookupAccountSidParameters(
	val systemName: String?,
	val sid: MemorySegment
)

data class LookupAccountSidReturn(
	val name: String,
	val referencedDomainName: String,
	val use: MappedEnumeration<UInt, _SID_NAME_USE>
)

val nativeLookupAccountSid = codingSpecific(
	nativeLookupAccountSidANSI,
	nativeLookupAccountSidWide
) { handle, parameters: LookupAccountSidParameters ->
	Arena.ofConfined().use { tempArena ->
		val lpSystemName =
			if (parameters.systemName == null) MemorySegment.NULL
			else tempArena.allocateFrom(parameters.systemName, winCharset)
		threadLocalDWORD0.set(DWORD, 0, 0)
		threadLocalDWORD1.set(DWORD, 0, 0)
		var status = handle.invokeExact(
			capturedStateSegment,
			lpSystemName,
			parameters.sid,
			MemorySegment.NULL,
			threadLocalDWORD0,
			MemorySegment.NULL,
			threadLocalDWORD1,
			threadLocalDWORD2
		) as Int
		if (status == 0) {
			if (win32LastError != 122) throwLastError()
		}
		val name = tempArena.allocate(TCHAR, threadLocalDWORD0.get(DWORD, 0).toLong())
		val referencedDomainName = tempArena.allocate(TCHAR, threadLocalDWORD1.get(DWORD, 0).toLong())
		status = handle.invokeExact(
			capturedStateSegment,
			lpSystemName,
			parameters.sid,
			name,
			threadLocalDWORD0,
			referencedDomainName,
			threadLocalDWORD1,
			threadLocalDWORD2
		) as Int
		if (status == 0) throwLastError()
		LookupAccountSidReturn(
			name.getString(0, winCharset),
			name.getString(0, winCharset),
			_SID_NAME_USE.entries.id(threadLocalDWORD2.get(DWORD, 0).toUInt())
		)
	}
}

val nativeFileEncryptionStatusWide: MethodHandle? = advapi32Lookup.getDowncall(
	nativeLinker, "FileEncryptionStatusW",
	arrayOf(
		BOOL,
		LPCWSTR.withName("lpFileName"),
		LPDWORD.withName("lpStatus")
	),
	listOf(
		gleCapture
	)
)

val nativeOpenEncryptedFileRawWide: MethodHandle? = advapi32Lookup.getDowncall(
	nativeLinker, "OpenEncryptedFileRawW",
	arrayOf(
		DWORD,
		LPCWSTR.withName("lpFileName"),
		ULONG.withName("ulFlags"),
		PVOID.withName("pvContext")
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

val nativeEncryptFileWide: MethodHandle? = advapi32Lookup.getDowncall(
	nativeLinker, "EncryptFileW",
	arrayOf(
		BOOL,
		LPCWSTR.withName("lpFileName")
	),
	listOf(
		gleCapture
	)
)

val nativeDecryptFileWide: MethodHandle? = advapi32Lookup.getDowncall(
	nativeLinker, "DecryptFileW",
	arrayOf(
		BOOL,
		LPCWSTR.withName("lpFileName"),
		DWORD.withName("dwReserved")
	),
	listOf(
		gleCapture
	)
)

val nativeEncryptionDisable: MethodHandle? = advapi32Lookup.getDowncall(
	nativeLinker, "EncryptionDisable",
	arrayOf(
		BOOL,
		LPCWSTR.withName("DirPath"),
		BOOL.withName("Disable")
	),
	listOf(
		gleCapture
	)
)

val nativeCloseEncryptedFileRaw: MethodHandle? = advapi32Lookup.getDowncallVoid(
	nativeLinker, "CloseEncryptedFileRaw",
	PVOID
)