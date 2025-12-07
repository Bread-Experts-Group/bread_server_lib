package org.bread_experts_group.api.system.socket.system.windows

import org.bread_experts_group.Flaggable
import org.bread_experts_group.Flaggable.Companion.from
import org.bread_experts_group.Flaggable.Companion.raw
import org.bread_experts_group.api.system.socket.system.SocketMonitor
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.throwLastWSAError
import org.bread_experts_group.ffi.windows.wsa.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

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

	val pendingAdditions = ConcurrentLinkedQueue<Triple<MemorySegment, Long, SocketMonitor>>()
	val pendingRemovals = ConcurrentLinkedQueue<Pair<MemorySegment, Long>>()
	val socketEvents = ConcurrentHashMap<Long, MemorySegment>()
	val eventSockets = ConcurrentHashMap<MemorySegment, Pair<Long, SocketMonitor>>()
	val changeEvent: MemorySegment = nativeWSACreateEvent!!.invokeExact(
		capturedStateSegment
	) as MemorySegment

	init {
		if (changeEvent == WSA_INVALID_EVENT) throwLastWSAError()
		Thread.ofPlatform().daemon().name("BSL Socket Management, for Windows").start {
			val arena = Arena.ofConfined()
			val networkEvents = arena.allocate(WSANETWORKEVENTS)

			while (true) {
				val triggeredEvent = Arena.ofConfined().use { tempArena ->
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
					waitArray.getAtIndex(ValueLayout.ADDRESS, index)
				}
				if (triggeredEvent == changeEvent) {
					val status = nativeWSAResetEvent!!.invokeExact(
						capturedStateSegment,
						triggeredEvent
					) as Int
					if (status == 0) throwLastWSAError()
					pendingRemovals.removeIf { (event, socket) ->
						val status = nativeWSACloseEvent!!.invokeExact(capturedStateSegment, event) as Int
						if (status == 0) throwLastWSAError()
						socketEvents.remove(socket)
						eventSockets.remove(event)
						true
					}
					pendingAdditions.removeIf { (event, socket, monitor) ->
						socketEvents[socket] = event
						eventSockets[event] = socket to monitor
						true
					}
					continue
				}
				val (socket, monitor) = eventSockets[triggeredEvent]!!
				val status = nativeWSAEnumNetworkEvents!!.invokeExact(
					capturedStateSegment,
					socket,
					triggeredEvent,
					networkEvents
				) as Int
				if (status != 0) throwLastWSAError()
				val events = SocketEvents.entries.from(
					WSANETWORKEVENTS_lNetworkEvents.get(networkEvents, 0L) as Int
				)
				for (event in events) {
					val semaphore = when (event) {
						SocketEvents.FD_READ -> monitor.read
						SocketEvents.FD_WRITE -> monitor.write
						SocketEvents.FD_CONNECT -> monitor.connect
						SocketEvents.FD_CLOSE -> {
							dropSocket(socket)
							if (monitor.read.availablePermits() < 1) monitor.read.release()
							if (monitor.write.availablePermits() < 1) monitor.write.release()
							monitor.close
						}

						SocketEvents.FD_ACCEPT -> monitor.accept
						else -> continue
					}
					if (semaphore.availablePermits() < 1) semaphore.release()
				}
			}
		}
	}

	fun notifyChange() {
		val status = nativeWSASetEvent!!.invokeExact(capturedStateSegment, changeEvent) as Int
		if (status == 0) throwLastWSAError()
	}

	fun addSocket(s: Long): SocketMonitor {
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
		val monitor = SocketMonitor()
		pendingAdditions.add(Triple(event, s, monitor))
		notifyChange()
		return monitor
	}

	fun dropSocket(s: Long) {
		val event = socketEvents[s] ?: return
		pendingRemovals.add(event to s)
		notifyChange()
	}
}