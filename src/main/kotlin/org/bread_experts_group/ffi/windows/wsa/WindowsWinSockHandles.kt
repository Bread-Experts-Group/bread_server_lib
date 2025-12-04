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

val nativeSocket: MethodHandle? = ws232Lookup.getDowncall(
	nativeLinker, "socket",
	arrayOf(
		SOCKET,
		ValueLayout.JAVA_INT.withName("af"),
		ValueLayout.JAVA_INT.withName("type"),
		ValueLayout.JAVA_INT.withName("protocol")
	),
	listOf(
		gleCapture
	)
)

val nativeWSAConnect: MethodHandle? = ws232Lookup.getDowncall(
	nativeLinker, "WSAConnect",
	arrayOf(
		ValueLayout.JAVA_INT,
		SOCKET.withName("s"),
		ValueLayout.ADDRESS.withName("name"), /* of type sockaddr */
		ValueLayout.JAVA_INT.withName("namelen"),
		ValueLayout.ADDRESS.withName("lpCallerData"), /* of type WSABUF */
		ValueLayout.ADDRESS.withName("lpCalleeData"), /* of type WSABUF */
		ValueLayout.ADDRESS.withName("lpSQOS"), /* of type QOS */
		ValueLayout.ADDRESS.withName("lpGQOS") /* of type QOS */
	),
	listOf(
		gleCapture
	)
)

val nativeWSAConnectByList: MethodHandle? = ws232Lookup.getDowncall(
	nativeLinker, "WSAConnectByList",
	arrayOf(
		BOOL,
		SOCKET.withName("s"),
		ValueLayout.ADDRESS.withName("SocketAddress"), /* of type SOCKET_ADDRESS_LIST */
		LPDWORD.withName("LocalAddressLength"),
		ValueLayout.ADDRESS.withName("LocalAddress"), /* of type SOCKADDR */
		LPDWORD.withName("RemoteAddressLength"),
		ValueLayout.ADDRESS.withName("RemoteAddress"), /* of type SOCKADDR */
		ValueLayout.ADDRESS.withName("timeout"), /* of type timeval */
		ValueLayout.ADDRESS.withName("Reserved") /* of type WSAOVERLAPPED */
	),
	listOf(
		gleCapture
	)
)

val nativeWSAHtons: MethodHandle? = ws232Lookup.getDowncall(
	nativeLinker, "WSAHtons",
	arrayOf(
		ValueLayout.JAVA_INT,
		SOCKET.withName("s"),
		ValueLayout.JAVA_SHORT.withName("hostshort"),
		ValueLayout.ADDRESS.withName("lpnetshort"),
	),
	listOf(
		gleCapture
	)
)

val nativeWSANtohs: MethodHandle? = ws232Lookup.getDowncall(
	nativeLinker, "WSANtohs",
	arrayOf(
		ValueLayout.JAVA_INT,
		SOCKET.withName("s"),
		ValueLayout.JAVA_SHORT.withName("netshort"),
		ValueLayout.ADDRESS.withName("lphostshort"),
	),
	listOf(
		gleCapture
	)
)

val nativeCloseSocket: MethodHandle? = ws232Lookup.getDowncall(
	nativeLinker, "closesocket",
	arrayOf(
		ValueLayout.JAVA_INT,
		SOCKET.withName("s")
	),
	listOf(
		gleCapture
	)
)

val nativeShutdown: MethodHandle? = ws232Lookup.getDowncall(
	nativeLinker, "shutdown",
	arrayOf(
		ValueLayout.JAVA_INT,
		SOCKET.withName("s"),
		ValueLayout.JAVA_INT.withName("how")
	),
	listOf(
		gleCapture
	)
)

val nativeSetSockOpt: MethodHandle? = ws232Lookup.getDowncall(
	nativeLinker, "setsockopt",
	arrayOf(
		ValueLayout.JAVA_INT,
		SOCKET.withName("s"),
		ValueLayout.JAVA_INT.withName("level"),
		ValueLayout.JAVA_INT.withName("optname"),
		ValueLayout.ADDRESS.withName("optval"),
		ValueLayout.JAVA_INT.withName("optlen")
	),
	listOf(
		gleCapture
	)
)

val nativeWSASend: MethodHandle? = ws232Lookup.getDowncall(
	nativeLinker, "WSASend",
	arrayOf(
		ValueLayout.JAVA_INT,
		SOCKET.withName("s"),
		ValueLayout.ADDRESS.withName("lpBuffers"), /* WSABUF */
		DWORD.withName("dwBufferCount"),
		LPDWORD.withName("lpNumberOfBytesSent"),
		DWORD.withName("dwFlags"),
		ValueLayout.ADDRESS.withName("lpOverlapped"), /* WSAOVERLAPPED */
		ValueLayout.ADDRESS.withName("lpCompletionRoutine") /* WSAOVERLAPPED_COMPLETION_ROUTINE */
	),
	listOf(
		gleCapture
	)
)

val nativeWSASendTo: MethodHandle? = ws232Lookup.getDowncall(
	nativeLinker, "WSASendTo",
	arrayOf(
		ValueLayout.JAVA_INT,
		SOCKET.withName("s"),
		ValueLayout.ADDRESS.withName("lpBuffers"), /* WSABUF */
		DWORD.withName("dwBufferCount"),
		LPDWORD.withName("lpNumberOfBytesSent"),
		DWORD.withName("dwFlags"),
		ValueLayout.ADDRESS.withName("lpTo"), /* sockaddr */
		ValueLayout.JAVA_INT.withName("iTolen"),
		ValueLayout.ADDRESS.withName("lpOverlapped"), /* WSAOVERLAPPED */
		ValueLayout.ADDRESS.withName("lpCompletionRoutine") /* WSAOVERLAPPED_COMPLETION_ROUTINE */
	),
	listOf(
		gleCapture
	)
)

val nativeWSARecv: MethodHandle? = ws232Lookup.getDowncall(
	nativeLinker, "WSARecv",
	arrayOf(
		ValueLayout.JAVA_INT,
		SOCKET.withName("s"),
		ValueLayout.ADDRESS.withName("lpBuffers"), /* WSABUF */
		DWORD.withName("dwBufferCount"),
		LPDWORD.withName("lpNumberOfBytesSent"),
		LPDWORD.withName("lpFlags"),
		ValueLayout.ADDRESS.withName("lpOverlapped"), /* WSAOVERLAPPED */
		ValueLayout.ADDRESS.withName("lpCompletionRoutine") /* WSAOVERLAPPED_COMPLETION_ROUTINE */
	),
	listOf(
		gleCapture
	)
)

val nativeWSARecvFrom: MethodHandle? = ws232Lookup.getDowncall(
	nativeLinker, "WSARecvFrom",
	arrayOf(
		ValueLayout.JAVA_INT,
		SOCKET.withName("s"),
		ValueLayout.ADDRESS.withName("lpBuffers"), /* WSABUF */
		DWORD.withName("dwBufferCount"),
		LPDWORD.withName("lpNumberOfBytesRecvd"),
		LPDWORD.withName("lpFlags"),
		ValueLayout.ADDRESS.withName("lpFrom"), /* sockaddr */
		LPINT.withName("lpFromlen"),
		ValueLayout.ADDRESS.withName("lpOverlapped"), /* WSAOVERLAPPED */
		ValueLayout.ADDRESS.withName("lpCompletionRoutine") /* WSAOVERLAPPED_COMPLETION_ROUTINE */
	),
	listOf(
		gleCapture
	)
)

val nativeBind: MethodHandle? = ws232Lookup.getDowncall(
	nativeLinker, "bind",
	arrayOf(
		ValueLayout.JAVA_INT,
		SOCKET.withName("s"),
		ValueLayout.ADDRESS.withName("addr"), /* sockaddr */
		ValueLayout.JAVA_INT.withName("namelen")
	),
	listOf(
		gleCapture
	)
)

val nativeListen: MethodHandle? = ws232Lookup.getDowncall(
	nativeLinker, "listen",
	arrayOf(
		ValueLayout.JAVA_INT,
		SOCKET.withName("s"),
		ValueLayout.JAVA_INT.withName("backlog")
	),
	listOf(
		gleCapture
	)
)

val nativeWSAAccept: MethodHandle? = ws232Lookup.getDowncall(
	nativeLinker, "WSAAccept",
	arrayOf(
		SOCKET,
		SOCKET.withName("s"),
		ValueLayout.ADDRESS.withName("addr"), /* sockaddr */
		LPINT.withName("addrlen"),
		ValueLayout.ADDRESS.withName("lpfnCondition"), /* CONDITIONPROC */
		DWORD_PTR.withName("dwCallbackData")
	),
	listOf(
		gleCapture
	)
)