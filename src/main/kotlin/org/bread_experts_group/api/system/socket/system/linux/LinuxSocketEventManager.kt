package org.bread_experts_group.api.system.socket.system.linux

import org.bread_experts_group.Flaggable
import org.bread_experts_group.Flaggable.Companion.from
import org.bread_experts_group.Flaggable.Companion.raw
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.linux.*
import org.bread_experts_group.ffi.posix.throwLastErrno
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object LinuxSocketEventManager {
	val ePollObject = nativeEPollCreate1!!.invokeExact(capturedStateSegment, 0) as Int
	val socketEvents = ConcurrentHashMap<Int, LinuxSocketMonitor>()

	const val EPOLL_CTL_ADD = 1
	const val EPOLL_CTL_DEL = 2

	enum class SocketEvents : Flaggable {
		EPOLLIN,
		X,
		EPOLLOUT;

		override val position: Long = 1L shl ordinal
	}

	init {
		if (ePollObject == -1) throwLastErrno()
		Thread.ofPlatform().daemon().name("BSL Socket Management, for Linux").start {
			val arena = Arena.ofConfined()
			val ePollEvents = arena.allocate(epoll_event)

			while (true) {
				val eventSize = nativeEPollWait!!.invokeExact(
					capturedStateSegment,
					ePollObject,
					ePollEvents,
					1, // TODO: Look into handling multiple events at once
					-1
				) as Int
				if (eventSize == -1) throwLastErrno()
				val monitor = socketEvents[epoll_event_data_u32.get(ePollEvents, 0L) as Int]!!
				for (event in SocketEvents.entries.from(epoll_event_events.get(ePollEvents, 0L) as Int)) when (event) {
					SocketEvents.EPOLLIN ->
						if (monitor.forAccept) {
							if (monitor.accept.availablePermits() < 1) monitor.accept.release()
						} else if (monitor.read.availablePermits() < 1) monitor.read.release()

					SocketEvents.EPOLLOUT -> {
						if (monitor.write.availablePermits() < 1) monitor.write.release()
					}

					else -> {}
				}
			}
		}
	}

	fun wakeupSocket(s: Int) {
		Arena.ofConfined().use { tempArena ->
			val event = tempArena.allocate(epoll_event)
			epoll_event_events.set(event, 0L, EnumSet.of(SocketEvents.EPOLLIN, SocketEvents.EPOLLOUT).raw().toInt())
			epoll_event_data_u32.set(event, 0L, s)
			val status = nativeEPollCtl!!.invokeExact(
				capturedStateSegment,
				ePollObject,
				EPOLL_CTL_ADD,
				s,
				event
			) as Int
			if (status != 0) throwLastErrno()
		}
	}

	fun addSocket(s: Int): LinuxSocketMonitor {
		val monitor = LinuxSocketMonitor(s)
		socketEvents[s] = monitor
		return monitor
	}

	fun dropSocket(s: Int) {
		val status = nativeEPollCtl!!.invokeExact(
			capturedStateSegment,
			ePollObject,
			EPOLL_CTL_DEL,
			s,
			MemorySegment.NULL
		) as Int
		if (status != 0) throwLastErrno()
		socketEvents.remove(s)
	}
}