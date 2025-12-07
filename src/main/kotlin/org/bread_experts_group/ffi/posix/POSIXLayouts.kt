package org.bread_experts_group.ffi.posix

import java.lang.foreign.AddressLayout
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.StructLayout
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle
import java.lang.invoke.VarHandle

const val SOMAXCONN = 128

val locale_t: AddressLayout = ValueLayout.ADDRESS

val addrinfo: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_INT.withName("ai_flags"),
	ValueLayout.JAVA_INT.withName("ai_family"),
	ValueLayout.JAVA_INT.withName("ai_socktype"),
	ValueLayout.JAVA_INT.withName("ai_protocol"),
	ValueLayout.JAVA_INT.withName("ai_addrlen"),
	MemoryLayout.paddingLayout(4),
	ValueLayout.ADDRESS.withName("ai_addr"),
	ValueLayout.ADDRESS.withName("ai_canonname"),
	ValueLayout.ADDRESS.withName("ai_next")
)
val addrinfo_ai_flags: VarHandle = addrinfo.varHandle(groupElement("ai_flags"))
val addrinfo_ai_family: VarHandle = addrinfo.varHandle(groupElement("ai_family"))
val addrinfo_ai_socktype: VarHandle = addrinfo.varHandle(groupElement("ai_socktype"))
val addrinfo_ai_protocol: VarHandle = addrinfo.varHandle(groupElement("ai_protocol"))
val addrinfo_ai_addrlen: VarHandle = addrinfo.varHandle(groupElement("ai_addrlen"))
val addrinfo_ai_addr: VarHandle = addrinfo.varHandle(groupElement("ai_addr"))
val addrinfo_ai_next: VarHandle = addrinfo.varHandle(groupElement("ai_next"))

val in6_addr: StructLayout = MemoryLayout.structLayout(
	MemoryLayout.sequenceLayout(16, ValueLayout.JAVA_BYTE).withName("s6_addr")
)

val sockaddr_in6: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_SHORT.withName("sin6_family"),
	ValueLayout.JAVA_SHORT.withName("sin6_port"),
	ValueLayout.JAVA_INT.withName("sin6_flowinfo"),
	in6_addr.withName("sin6_addr"),
	ValueLayout.JAVA_INT.withName("sin6_scope_id")
)
val sockaddr_in6_sin6_family: VarHandle = sockaddr_in6.varHandle(groupElement("sin6_family"))
val sockaddr_in6_sin6_port: VarHandle = sockaddr_in6.varHandle(groupElement("sin6_port"))
val sockaddr_in6_sin6_addr: MethodHandle = sockaddr_in6.sliceHandle(groupElement("sin6_addr"))

val iovec: StructLayout = MemoryLayout.structLayout(
	ValueLayout.ADDRESS.withName("iov_base"),
	ValueLayout.JAVA_LONG.withName("iov_len")
)
val iovec_iov_base: VarHandle = iovec.varHandle(groupElement("iov_base"))
val iovec_iov_len: VarHandle = iovec.varHandle(groupElement("iov_len"))

val msghdr: StructLayout = MemoryLayout.structLayout(
	ValueLayout.ADDRESS.withName("msg_name"),
	ValueLayout.JAVA_INT.withName("msg_namelen"),
	MemoryLayout.paddingLayout(4),
	ValueLayout.ADDRESS.withName("msg_iov"), /* of iovec */
	ValueLayout.JAVA_LONG.withName("msg_iovlen"),
	ValueLayout.ADDRESS.withName("msg_control"),
	ValueLayout.JAVA_LONG.withName("msg_controllen"),
	ValueLayout.JAVA_INT.withName("msg_flags")
)
val msghdr_msg_iov: VarHandle = msghdr.varHandle(groupElement("msg_iov"))
val msghdr_msg_iovlen: VarHandle = msghdr.varHandle(groupElement("msg_iovlen"))