package org.bread_experts_group.api.system.socket.windows

import org.bread_experts_group.Flaggable
import org.bread_experts_group.Flaggable.Companion.from
import org.bread_experts_group.Flaggable.Companion.raw
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.throwLastWSAError
import org.bread_experts_group.ffi.windows.wsa.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.util.*
import java.util.concurrent.ConcurrentHashMap

internal object WindowsSocketEventManager {
	enum class SocketEvents : Flaggable {
		FD_READ,
		FD_WRITE,
		FD_OOB,
		FD_ACCEPT,
		FD_CONNECT,
		FD_CLOSE,
		FD_QOS,
		FD_GROUP_QOS,
		FD_ROUTING_INTERFACE_CHANGE,
		FD_ADDRESS_LIST_CHANGE;

		override val position: Long = 1L shl ordinal
	}

	val socketEvents = ConcurrentHashMap<Long, MemorySegment>()
	val eventSockets = ConcurrentHashMap<MemorySegment, Pair<Long, WindowsSocketMonitor>>()
	val changeEvent: MemorySegment = nativeWSACreateEvent!!.invokeExact(
		capturedStateSegment
	) as MemorySegment

	init {
		if (changeEvent == WSA_INVALID_EVENT) throwLastWSAError()
		Thread.ofPlatform().daemon().name("BSL Socket Management, for Windows").start {
			while (true) {
				Arena.ofConfined().use { tempArena ->
					val waitArray = tempArena.allocate(WSAEVENT, 1L + eventSockets.size)
					waitArray.setAtIndex(ValueLayout.ADDRESS, 0, changeEvent)
					eventSockets.keys.forEachIndexed { i, event ->
						waitArray.setAtIndex(ValueLayout.ADDRESS, i + 1L, event)
					}
					var status = nativeWSAWaitForMultipleEvents!!.invokeExact(
						capturedStateSegment,
						1 + eventSockets.size,
						waitArray,
						0,
						WSA_INFINITE,
						0
					) as Int
					if (status == WSA_WAIT_FAILED) throwLastWSAError()
					val index = status - WSA_WAIT_EVENT_0.toLong()
					val triggeredEvent = waitArray.getAtIndex(ValueLayout.ADDRESS, index)
					if (index == 0L) {
						val status = nativeWSAResetEvent!!.invokeExact(
							capturedStateSegment,
							triggeredEvent
						) as Int
						if (status == 0) throwLastWSAError()
						continue
					}
					val networkEvents = tempArena.allocate(WSANETWORKEVENTS)
					val (socket, monitor) = eventSockets[triggeredEvent]!!
					status = nativeWSAEnumNetworkEvents!!.invokeExact(
						capturedStateSegment,
						socket,
						triggeredEvent,
						networkEvents
					) as Int
					if (status != 0) throwLastWSAError()
					val events = SocketEvents.entries.from(
						WSANETWORKEVENTS_lNetworkEvents.get(networkEvents, 0L) as Int
					)
					println("$events")
					for (event in events) {
						val semaphore = when (event) {
							SocketEvents.FD_READ -> monitor.read
							SocketEvents.FD_WRITE -> monitor.write
							SocketEvents.FD_CONNECT -> monitor.connect
							SocketEvents.FD_CLOSE -> monitor.close
							SocketEvents.FD_ACCEPT -> monitor.accept
							else -> continue
						}
						if (semaphore.availablePermits() < 1) semaphore.release()
					}
				}
			}
		}
	}

	fun notifyChange() {
		val status = nativeWSASetEvent!!.invokeExact(capturedStateSegment, changeEvent) as Int
		if (status == 0) throwLastWSAError()
	}

	fun addSocket(s: Long): WindowsSocketMonitor {
		val event = nativeWSACreateEvent!!.invokeExact(
			capturedStateSegment
		) as MemorySegment
		if (event == WSA_INVALID_EVENT) throwLastWSAError()
		val status = nativeWSAEventSelect!!.invokeExact(
			capturedStateSegment,
			s,
			event,
			EnumSet.of(
				SocketEvents.FD_READ,
				SocketEvents.FD_WRITE,
				SocketEvents.FD_ACCEPT,
				SocketEvents.FD_CONNECT,
				SocketEvents.FD_CLOSE
			).raw().toInt()
		) as Int
		if (status != 0) throwLastWSAError()
		socketEvents[s] = event
		val monitor = WindowsSocketMonitor()
		eventSockets[event] = s to monitor
		notifyChange()
		return monitor
	}

	fun dropSocket(s: Long) {
		val event = socketEvents.remove(s) ?: return
		eventSockets.remove(event)
		val status = nativeWSACloseEvent!!.invokeExact(capturedStateSegment, event) as Int
		if (status == 0) throwLastWSAError()
		notifyChange()
	}
}