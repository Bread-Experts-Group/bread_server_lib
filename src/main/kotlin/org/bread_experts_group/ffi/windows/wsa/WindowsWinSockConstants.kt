package org.bread_experts_group.ffi.windows.wsa

import org.bread_experts_group.ffi.GUID
import org.bread_experts_group.ffi.globalArena

const val WSAEWOULDBLOCK = 10035
const val WSAECONNRESET = 10054
const val WSAENOTCONN = 10057

const val SIO_GET_EXTENSION_FUNCTION_POINTER = 0xC8000006.toInt()

@OptIn(ExperimentalUnsignedTypes::class)
val WSAID_CONNECTEX = GUID(
	0x25A207B9u, 0xDDF3u, 0x4660u,
	ubyteArrayOf(0x8Eu, 0xE9u),
	ubyteArrayOf(0x76u, 0xE5u, 0x8Cu, 0x74u, 0x06u, 0x3Eu)
).allocate(globalArena)

@OptIn(ExperimentalUnsignedTypes::class)
val WSAID_ACCEPTEX = GUID(
	0xB5367DF1u, 0xCBACu, 0x11CFu,
	ubyteArrayOf(0x95u, 0xCAu),
	ubyteArrayOf(0x00u, 0x80u, 0x5Fu, 0x48u, 0xA1u, 0x92u)
).allocate(globalArena)

@OptIn(ExperimentalUnsignedTypes::class)
val WSAID_GETACCEPTEXSOCKADDRS = GUID(
	0xB5367DF2u, 0xCBACu, 0x11CFu,
	ubyteArrayOf(0x95u, 0xCAu),
	ubyteArrayOf(0x00u, 0x80u, 0x5Fu, 0x48u, 0xA1u, 0x92u)
).allocate(globalArena)