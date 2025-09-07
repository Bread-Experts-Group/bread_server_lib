package org.bread_experts_group.api.socket.windows

import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.StructLayout
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle
import java.lang.invoke.VarHandle

val wsaDataStruct: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_SHORT.withName("wVersion"),
	ValueLayout.JAVA_SHORT.withName("wHighVersion"),
)
val wVersionHandle: VarHandle = wsaDataStruct.varHandle(groupElement("wVersion"))
val wHighVersionHandle: VarHandle = wsaDataStruct.varHandle(groupElement("wHighVersion"))

val wsaBufferStruct: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_INT.withName("len"),
	MemoryLayout.paddingLayout(4),
	ValueLayout.ADDRESS.withName("buf")
)
val wLenHandle: VarHandle = wsaBufferStruct.varHandle(groupElement("len"))
val wBufHandle: VarHandle = wsaBufferStruct.varHandle(groupElement("buf"))

val wsaV4SockAddrStruct: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_SHORT.withName("sa_family"),
	MemoryLayout.sequenceLayout(14, ValueLayout.JAVA_BYTE).withName("sa_data")
)
val wSaV4FamilyHandle: VarHandle = wsaV4SockAddrStruct.varHandle(groupElement("sa_family"))
//val wSaV4DataHandle: MethodHandle = wsaV4SockAddrStruct.sliceHandle(groupElement("sa_data"))

val wsaV4SockAddrInStruct: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_SHORT.withName("sin_family"),
	ValueLayout.JAVA_SHORT.withName("sin_port"),
	MemoryLayout.sequenceLayout(4, ValueLayout.JAVA_BYTE).withName("sin_addr"),
	MemoryLayout.paddingLayout(8) // sin_zero
)

//val wSaV4PortHandle: VarHandle = wsaV4SockAddrInStruct.varHandle(groupElement("sin_port"))
val wSaV4AddrHandle: MethodHandle = wsaV4SockAddrInStruct.sliceHandle(groupElement("sin_addr"))

val wsaV6SockAddrInStruct: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_SHORT.withName("sin6_family"),
	ValueLayout.JAVA_SHORT.withName("sin6_port"),
	ValueLayout.JAVA_INT.withName("sin6_flowinfo"),
	MemoryLayout.sequenceLayout(16, ValueLayout.JAVA_BYTE).withName("sin6_addr"),
	ValueLayout.JAVA_INT.withName("sin6_scope_id"),
)
val wSaV6FamilyHandle: VarHandle = wsaV6SockAddrInStruct.varHandle(groupElement("sin6_family"))
val wSaV6AddrHandle: MethodHandle = wsaV6SockAddrInStruct.sliceHandle(groupElement("sin6_addr"))