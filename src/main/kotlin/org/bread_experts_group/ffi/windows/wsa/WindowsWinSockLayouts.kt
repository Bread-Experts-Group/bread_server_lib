package org.bread_experts_group.ffi.windows.wsa

import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.MemorySegment
import java.lang.foreign.StructLayout
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle
import java.lang.invoke.VarHandle

const val WSADESCRIPTION_LEN = 256
const val WSASYS_STATUS_LEN = 128

val SOCKET = UINT_PTR
const val INVALID_SOCKET = 0L.inv()

val WSAData: StructLayout = MemoryLayout.structLayout(
	WORD.withName("wVersion"),
	WORD.withName("wHighVersion"),
	ValueLayout.JAVA_SHORT.withName("iMaxSockets"),
	ValueLayout.JAVA_SHORT.withName("iMaxUdpDg"),
	ValueLayout.ADDRESS.withName("lpVendorInfo"),
	MemoryLayout.sequenceLayout(WSADESCRIPTION_LEN + 1L, ValueLayout.JAVA_BYTE).withName("szDescription"),
	MemoryLayout.sequenceLayout(WSASYS_STATUS_LEN + 1L, ValueLayout.JAVA_BYTE).withName("szSystemStatus"),
)
val WSAData_szDescription: MethodHandle = WSAData.sliceHandle(groupElement("szDescription"))
val WSAData_szSystemStatus: MethodHandle = WSAData.sliceHandle(groupElement("szSystemStatus"))

const val SOCKET_ERROR = -1
const val WSAPROTOCOL_LEN = 255
const val MAX_PROTOCOL_CHAIN = 7

val WSAPROTOCOLCHAIN: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_INT.withName("ChainLen"),
	MemoryLayout.sequenceLayout(MAX_PROTOCOL_CHAIN.toLong(), DWORD).withName("ChainEntries")
)

val WSAPROTOCOL_INFOW: StructLayout = MemoryLayout.structLayout(
	DWORD.withName("dwServiceFlags1"),
	DWORD.withName("dwServiceFlags2"),
	DWORD.withName("dwServiceFlags3"),
	DWORD.withName("dwServiceFlags4"),
	DWORD.withName("dwProviderFlags"),
	GUID.withName("ProviderId"),
	DWORD.withName("dwCatalogEntryId"),
	WSAPROTOCOLCHAIN.withName("ProtocolChain"),
	ValueLayout.JAVA_INT.withName("iVersion"),
	ValueLayout.JAVA_INT.withName("iAddressFamily"),
	ValueLayout.JAVA_INT.withName("iMaxSockAddr"),
	ValueLayout.JAVA_INT.withName("iMinSockAddr"),
	ValueLayout.JAVA_INT.withName("iSocketType"),
	ValueLayout.JAVA_INT.withName("iProtocol"),
	ValueLayout.JAVA_INT.withName("iProtocolMaxOffset"),
	ValueLayout.JAVA_INT.withName("iNetworkByteOrder"),
	ValueLayout.JAVA_INT.withName("iSecurityScheme"),
	DWORD.withName("dwMessageSize"),
	DWORD.withName("dwProviderReserved"),
	MemoryLayout.sequenceLayout(WSAPROTOCOL_LEN + 1L, WCHAR).withName("szProtocol")
)
val WSAPROTOCOL_INFOW_iAddressFamily: VarHandle = WSAPROTOCOL_INFOW.varHandle(groupElement("iAddressFamily"))
val WSAPROTOCOL_INFOW_iSocketType: VarHandle = WSAPROTOCOL_INFOW.varHandle(groupElement("iSocketType"))
val WSAPROTOCOL_INFOW_iProtocol: VarHandle = WSAPROTOCOL_INFOW.varHandle(groupElement("iProtocol"))
val WSAPROTOCOL_INFOW_szProtocol: MethodHandle = WSAPROTOCOL_INFOW.sliceHandle(groupElement("szProtocol"))

val WSANAMESPACE_INFOW: StructLayout = MemoryLayout.structLayout(
	GUID.withName("NSProviderId"),
	DWORD.withName("dwNameSpace"),
	BOOL.withName("fActive"),
	DWORD.withName("dwVersion"),
	MemoryLayout.paddingLayout(4),
	LPWSTR.withName("lpszIdentifier")
)
val WSANAMESPACE_INFOW_NSProviderId: MethodHandle = WSANAMESPACE_INFOW.sliceHandle(groupElement("NSProviderId"))
val WSANAMESPACE_INFOW_dwNameSpace: VarHandle = WSANAMESPACE_INFOW.varHandle(groupElement("dwNameSpace"))
val WSANAMESPACE_INFOW_fActive: VarHandle = WSANAMESPACE_INFOW.varHandle(groupElement("fActive"))
val WSANAMESPACE_INFOW_dwVersion: VarHandle = WSANAMESPACE_INFOW.varHandle(groupElement("dwVersion"))
val WSANAMESPACE_INFOW_lpszIdentifier: VarHandle = WSANAMESPACE_INFOW.varHandle(groupElement("lpszIdentifier"))

val ADDRINFOEXW: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_INT.withName("ai_flags"),
	ValueLayout.JAVA_INT.withName("ai_family"),
	ValueLayout.JAVA_INT.withName("ai_socktype"),
	ValueLayout.JAVA_INT.withName("ai_protocol"),
	ValueLayout.JAVA_LONG.withName("ai_addrlen"),
	PWSTR.withName("ai_canonname"),
	ValueLayout.ADDRESS.withName("ai_addr"),
	ValueLayout.ADDRESS.withName("ai_blob"),
	ValueLayout.JAVA_LONG.withName("ai_bloblen"),
	ValueLayout.ADDRESS.withName("ai_provider"),
	ValueLayout.ADDRESS.withName("ai_next")
)
val ADDRINFOEXW_ai_flags: VarHandle = ADDRINFOEXW.varHandle(groupElement("ai_flags"))
val ADDRINFOEXW_ai_family: VarHandle = ADDRINFOEXW.varHandle(groupElement("ai_family"))
val ADDRINFOEXW_ai_socktype: VarHandle = ADDRINFOEXW.varHandle(groupElement("ai_socktype"))
val ADDRINFOEXW_ai_protocol: VarHandle = ADDRINFOEXW.varHandle(groupElement("ai_protocol"))
val ADDRINFOEXW_ai_canonname: VarHandle = ADDRINFOEXW.varHandle(groupElement("ai_canonname"))
val ADDRINFOEXW_ai_addrlen: VarHandle = ADDRINFOEXW.varHandle(groupElement("ai_addrlen"))
val ADDRINFOEXW_ai_addr: VarHandle = ADDRINFOEXW.varHandle(groupElement("ai_addr"))
val ADDRINFOEXW_ai_next: VarHandle = ADDRINFOEXW.varHandle(groupElement("ai_next"))

val ADDRINFOEX2W: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_INT.withName("ai_flags"),
	ValueLayout.JAVA_INT.withName("ai_family"),
	ValueLayout.JAVA_INT.withName("ai_socktype"),
	ValueLayout.JAVA_INT.withName("ai_protocol"),
	ValueLayout.JAVA_LONG.withName("ai_addrlen"),
	PWSTR.withName("ai_canonname"),
	ValueLayout.ADDRESS.withName("ai_addr"),
	ValueLayout.ADDRESS.withName("ai_blob"),
	ValueLayout.JAVA_LONG.withName("ai_bloblen"),
	ValueLayout.ADDRESS.withName("ai_provider"),
	ValueLayout.ADDRESS.withName("ai_next"),
	ValueLayout.JAVA_INT.withName("ai_version"),
	MemoryLayout.paddingLayout(4),
	PWSTR.withName("ai_fqdn")
)
val ADDRINFOEX2W_ai_canonname: VarHandle = ADDRINFOEX2W.varHandle(groupElement("ai_canonname"))
val ADDRINFOEX2W_ai_fqdn: VarHandle = ADDRINFOEX2W.varHandle(groupElement("ai_fqdn"))
val ADDRINFOEX2W_ai_addrlen: VarHandle = ADDRINFOEX2W.varHandle(groupElement("ai_addrlen"))
val ADDRINFOEX2W_ai_addr: VarHandle = ADDRINFOEX2W.varHandle(groupElement("ai_addr"))
val ADDRINFOEX2W_ai_next: VarHandle = ADDRINFOEX2W.varHandle(groupElement("ai_next"))

val ADDRESS_FAMILY = USHORT
val IN_ADDR = MemoryLayout.sequenceLayout(4, ValueLayout.JAVA_BYTE)

val sockaddr_in: StructLayout = MemoryLayout.structLayout(
	ADDRESS_FAMILY.withName("sin_family"),
	USHORT.withName("sin_port"),
	IN_ADDR.withName("sin_addr"),
	MemoryLayout.sequenceLayout(8, CHAR).withName("sin_zero"),
)

val sockaddr_in_sin_family: VarHandle = sockaddr_in.varHandle(groupElement("sin_family"))
val sockaddr_in_sin_port: VarHandle = sockaddr_in.varHandle(groupElement("sin_port"))
val sockaddr_in_sin_addr: MethodHandle = sockaddr_in.sliceHandle(groupElement("sin_addr"))

val SCOPE_ID: StructLayout = MemoryLayout.structLayout(
	LONG.withName("Value")
)

val IN6_ADDR: StructLayout = MemoryLayout.structLayout(
	MemoryLayout.unionLayout(
		MemoryLayout.sequenceLayout(16, UCHAR).withName("Byte"),
		MemoryLayout.sequenceLayout(8, USHORT).withName("Word")
	).withName("u")
)

val sockaddr_in6: StructLayout = MemoryLayout.structLayout(
	ADDRESS_FAMILY.withName("sin6_family"),
	USHORT.withName("sin6_port"),
	ULONG.withName("sin6_flowinfo"),
	IN6_ADDR.withName("sin6_addr"),
	MemoryLayout.unionLayout(
		ULONG.withByteAlignment(1).withName("sin6_scope_id"),
		SCOPE_ID.withName("sin6_scope_struct")
	).withName("union0")
)
val sockaddr_in6_sin6_family: VarHandle = sockaddr_in6.varHandle(groupElement("sin6_family"))
val sockaddr_in6_sin6_port: VarHandle = sockaddr_in6.varHandle(groupElement("sin6_port"))
val sockaddr_in6_sin6_addr_Byte: MethodHandle = sockaddr_in6.sliceHandle(
	groupElement("sin6_addr"),
	groupElement("u"),
	groupElement("Byte")
)

val SOCKET_ADDRESS: StructLayout = MemoryLayout.structLayout(
	ValueLayout.ADDRESS.withName("lpSockaddr"), /* of type sockaddr */
	ValueLayout.JAVA_INT.withName("iSockaddrLength"),
	MemoryLayout.paddingLayout(4),
)
val SOCKET_ADDRESS_lpSockaddr: VarHandle = SOCKET_ADDRESS.varHandle(groupElement("lpSockaddr"))
val SOCKET_ADDRESS_iSockaddrLength: VarHandle = SOCKET_ADDRESS.varHandle(groupElement("iSockaddrLength"))

val WSABUF: StructLayout = MemoryLayout.structLayout(
	ULONG.withName("len"),
	MemoryLayout.paddingLayout(4),
	ValueLayout.ADDRESS.withName("buf")
)
val WSABUF_len: VarHandle = WSABUF.varHandle(groupElement("len"))
val WSABUF_buf: VarHandle = WSABUF.varHandle(groupElement("buf"))

val WSAEVENT = HANDLE
val WSA_INVALID_EVENT: MemorySegment = MemorySegment.NULL

const val WSA_INFINITE = INFINITE
const val WSA_WAIT_FAILED = WAIT_FAILED

const val WSA_WAIT_EVENT_0 = WAIT_OBJECT_0

const val FD_MAX_EVENTS = 10
val WSANETWORKEVENTS: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_INT.withName("lNetworkEvents"),
	MemoryLayout.sequenceLayout(
		FD_MAX_EVENTS.toLong(),
		ValueLayout.JAVA_INT
	).withName("iErrorCode")
)
val WSANETWORKEVENTS_lNetworkEvents: VarHandle = WSANETWORKEVENTS.varHandle(groupElement("lNetworkEvents"))