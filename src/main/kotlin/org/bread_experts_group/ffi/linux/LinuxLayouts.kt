package org.bread_experts_group.ffi.linux

import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.StructLayout
import java.lang.foreign.UnionLayout
import java.lang.foreign.ValueLayout
import java.lang.invoke.VarHandle

val epoll_data_t: UnionLayout = MemoryLayout.unionLayout(
	ValueLayout.ADDRESS.withName("ptr"),
	ValueLayout.JAVA_INT.withName("fd"),
	ValueLayout.JAVA_INT.withName("u32"),
	ValueLayout.JAVA_LONG.withName("u64")
)

val epoll_event: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_INT.withName("events"),
	MemoryLayout.paddingLayout(4),
	epoll_data_t.withName("data")
)
val epoll_event_events: VarHandle = epoll_event.varHandle(groupElement("events"))
val epoll_event_data_u32: VarHandle = epoll_event.varHandle(
	groupElement("data"),
	groupElement("u32")
)