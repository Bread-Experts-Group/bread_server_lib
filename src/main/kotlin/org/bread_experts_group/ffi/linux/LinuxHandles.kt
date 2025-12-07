package org.bread_experts_group.ffi.linux

import org.bread_experts_group.ffi.cLookup
import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.nativeLinker
import org.bread_experts_group.ffi.posix.ernCapture
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

val nativeEPollCreate1: MethodHandle? = cLookup.getDowncall(
	nativeLinker, "epoll_create1",
	arrayOf(
		ValueLayout.JAVA_INT,
		ValueLayout.JAVA_INT.withName("flags")
	),
	listOf(
		ernCapture
	)
)

val nativeEPollWait: MethodHandle? = cLookup.getDowncall(
	nativeLinker, "epoll_wait",
	arrayOf(
		ValueLayout.JAVA_INT,
		ValueLayout.JAVA_INT.withName("epfd"),
		ValueLayout.ADDRESS.withName("events"), /* of epoll_event */
		ValueLayout.JAVA_INT.withName("maxevents"),
		ValueLayout.JAVA_INT.withName("timeout")
	),
	listOf(
		ernCapture
	)
)

val nativeEPollCtl: MethodHandle? = cLookup.getDowncall(
	nativeLinker, "epoll_ctl",
	arrayOf(
		ValueLayout.JAVA_INT,
		ValueLayout.JAVA_INT.withName("epfd"),
		ValueLayout.JAVA_INT.withName("op"),
		ValueLayout.JAVA_INT.withName("fd"),
		ValueLayout.ADDRESS.withName("event")
	), /* of epoll_event */
	listOf(
		ernCapture
	)
)