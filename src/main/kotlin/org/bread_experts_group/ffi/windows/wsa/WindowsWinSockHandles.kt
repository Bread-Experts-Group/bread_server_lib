package org.bread_experts_group.ffi.windows.wsa

import org.bread_experts_group.ffi.*
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

private val ws232Lookup: SymbolLookup? = globalArena.getLookup("Ws2_32.dll")

val nativeWSAStartup: MethodHandle? = ws232Lookup.getDowncall(
	nativeLinker, "WSAStartup",
	arrayOf(
		ValueLayout.JAVA_INT,
		WORD, ValueLayout.ADDRESS /* of WSADATA */
	),
	listOf()
)

val nativeWSCEnumProtocols: MethodHandle? = ws232Lookup.getDowncall(
	nativeLinker, "WSCEnumProtocols",
	arrayOf(
		ValueLayout.JAVA_INT,
		LPINT, ValueLayout.ADDRESS /* of WSAPROTOCOL_INFOW */, LPDWORD, LPINT
	),
	listOf()
)

val nativeWSAEnumNameSpaceProvidersW: MethodHandle? = ws232Lookup.getDowncall(
	nativeLinker, "WSAEnumNameSpaceProvidersW",
	arrayOf(
		ValueLayout.JAVA_INT,
		LPDWORD, ValueLayout.ADDRESS /* of WSANAMESPACE_INFOW */
	),
	listOf()
)

val nativeGetAddrInfoExW: MethodHandle? = ws232Lookup.getDowncall(
	nativeLinker, "GetAddrInfoExW",
	arrayOf(
		ValueLayout.JAVA_INT,
		LPCWSTR, LPCWSTR, DWORD, ValueLayout.ADDRESS /* of GUID */, ValueLayout.ADDRESS /* of ADDRINFOEXW */,
		ValueLayout.ADDRESS /* of ADDRINFOEXW */, ValueLayout.ADDRESS /* of timeval */,
		ValueLayout.ADDRESS /* of OVERLAPPED */, ValueLayout.ADDRESS /* of LOOKUPSERVICE_COMPLETION_ROUTINE */,
		ValueLayout.ADDRESS /* of HANDLE */
	),
	listOf()
)

val nativeFreeAddrInfoExW: MethodHandle? = ws232Lookup.getDowncallVoid(
	nativeLinker, "FreeAddrInfoExW",
	ValueLayout.ADDRESS /* of ADDRINFOEXW */
)