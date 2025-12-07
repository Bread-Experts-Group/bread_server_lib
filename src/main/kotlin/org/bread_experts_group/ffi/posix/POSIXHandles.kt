package org.bread_experts_group.ffi.posix

import org.bread_experts_group.ffi.*
import java.lang.foreign.Linker
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle
import java.lang.invoke.VarHandle

val ernCapture: Linker.Option? = try {
	Linker.Option.captureCallState("errno")
} catch (_: IllegalArgumentException) {
	null
}
val nativeErrno: VarHandle by lazy {
	capturedStateLayout.varHandle(groupElement("errno"))
}

val nativeNewLocale: MethodHandle? = cLookup.getDowncall(
	nativeLinker, "newlocale",
	arrayOf(
		locale_t,
		ValueLayout.JAVA_INT.withName("category_mask"),
		ValueLayout.ADDRESS.withName("locale"), /* of char */
		locale_t.withName("base")
	),
	listOf(
		ernCapture
	)
)

val nativeFreeLocale: MethodHandle? = cLookup.getDowncallVoid(
	nativeLinker, "freelocale",
	locale_t.withName("locobj")
)

val nativeStrErrorNameNP: MethodHandle? = cLookup.getDowncall(
	nativeLinker, "strerrorname_np",
	ValueLayout.ADDRESS, /* of char */
	ValueLayout.JAVA_INT.withName("errnum")
)

val nativeStrErrorL: MethodHandle? = cLookup.getDowncall(
	nativeLinker, "strerror_l",
	ValueLayout.ADDRESS, /* of char */
	ValueLayout.JAVA_INT.withName("errnum"),
	locale_t.withName("locale")
)

val nativeSocket: MethodHandle? = cLookup.getDowncall(
	nativeLinker, "socket",
	arrayOf(
		ValueLayout.JAVA_INT,
		ValueLayout.JAVA_INT.withName("domain"),
		ValueLayout.JAVA_INT.withName("type"),
		ValueLayout.JAVA_INT.withName("protocol")
	),
	listOf(
		ernCapture
	)
)

val nativeClose: MethodHandle? = cLookup.getDowncall(
	nativeLinker, "close",
	arrayOf(
		ValueLayout.JAVA_INT,
		ValueLayout.JAVA_INT.withName("fd")
	),
	listOf(
		ernCapture
	)
)

val nativeGetAddrInfo: MethodHandle? = cLookup.getDowncall(
	nativeLinker, "getaddrinfo",
	arrayOf(
		ValueLayout.JAVA_INT,
		ValueLayout.ADDRESS.withName("node"), /* of char */
		ValueLayout.ADDRESS.withName("service"), /* of char */
		ValueLayout.ADDRESS.withName("hints"), /* of addrinfo */
		ValueLayout.ADDRESS.withName("res"), /* of addrinfo */
	),
	listOf(
		ernCapture
	)
)

val nativeFreeAddrInfo: MethodHandle? = cLookup.getDowncallVoid(
	nativeLinker, "freeaddrinfo",
	ValueLayout.ADDRESS.withName("res") /* of addrinfo */
)

val nativeSetSockOpt: MethodHandle? = cLookup.getDowncall(
	nativeLinker, "setsockopt",
	arrayOf(
		ValueLayout.JAVA_INT,
		ValueLayout.JAVA_INT.withName("socket"),
		ValueLayout.JAVA_INT.withName("level"),
		ValueLayout.JAVA_INT.withName("option_name"),
		ValueLayout.ADDRESS.withName("option_value"),
		ValueLayout.JAVA_INT.withName("option_len")
	),
	listOf(
		ernCapture
	)
)

val nativeBind: MethodHandle? = cLookup.getDowncall(
	nativeLinker, "bind",
	arrayOf(
		ValueLayout.JAVA_INT,
		ValueLayout.JAVA_INT.withName("sockfd"),
		ValueLayout.ADDRESS.withName("addr"), /* of sockaddr */
		ValueLayout.JAVA_INT.withName("addrlen")
	),
	listOf(
		ernCapture
	)
)

val nativeListen: MethodHandle? = cLookup.getDowncall(
	nativeLinker, "listen",
	arrayOf(
		ValueLayout.JAVA_INT,
		ValueLayout.JAVA_INT.withName("sockfd"),
		ValueLayout.JAVA_INT.withName("backlog")
	),
	listOf(
		ernCapture
	)
)

val nativeAccept: MethodHandle? = cLookup.getDowncall(
	nativeLinker, "accept",
	arrayOf(
		ValueLayout.JAVA_INT,
		ValueLayout.JAVA_INT.withName("sockfd"),
		ValueLayout.ADDRESS.withName("addr"), /* of sockaddr */
		ValueLayout.ADDRESS.withName("addrlen") /* of socklen_t */
	),
	listOf(
		ernCapture
	)
)

val nativeRecvMsg: MethodHandle? = cLookup.getDowncall(
	nativeLinker, "recvmsg",
	arrayOf(
		ValueLayout.JAVA_LONG,
		ValueLayout.JAVA_INT.withName("sockfd"),
		ValueLayout.ADDRESS.withName("msg"),
		ValueLayout.JAVA_INT.withName("flags")
	),
	listOf(
		ernCapture
	)
)

val nativeSendMsg: MethodHandle? = cLookup.getDowncall(
	nativeLinker, "sendmsg",
	arrayOf(
		ValueLayout.JAVA_LONG,
		ValueLayout.JAVA_INT.withName("sockfd"),
		ValueLayout.ADDRESS.withName("msg"),
		ValueLayout.JAVA_INT.withName("flags")
	),
	listOf(
		ernCapture
	)
)

val nativeHtons: MethodHandle? = cLookup.getDowncall(
	nativeLinker, "htons",
	ValueLayout.JAVA_SHORT,
	ValueLayout.JAVA_SHORT.withName("hostshort")
)

val nativeShutdown: MethodHandle? = cLookup.getDowncall(
	nativeLinker, "shutdown",
	arrayOf(
		ValueLayout.JAVA_INT,
		ValueLayout.JAVA_INT.withName("sockfd"),
		ValueLayout.JAVA_INT.withName("how")
	),
	listOf(
		ernCapture
	)
)