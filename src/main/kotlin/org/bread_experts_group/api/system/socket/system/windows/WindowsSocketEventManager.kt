package org.bread_experts_group.api.system.socket.system.windows

import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.threadLocalPTR
import org.bread_experts_group.ffi.windows.*
import org.bread_experts_group.ffi.windows.wsa.WSAOVERLAPPED
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.MemorySegment
import java.lang.foreign.StructLayout
import java.lang.foreign.ValueLayout
import java.lang.invoke.VarHandle
import java.util.concurrent.ConcurrentHashMap

internal object WindowsSocketEventManager {
	val WSAOVERLAPPEDEncapsulate: StructLayout = MemoryLayout.structLayout(
		WSAOVERLAPPED.withName("overlapped"),
		DWORD.withName("operation"),
		DWORD.withName("identification")
	)
	val WSAOVERLAPPEDEncapsulate_operation: VarHandle = WSAOVERLAPPEDEncapsulate.varHandle(
		groupElement("operation")
	)
	val WSAOVERLAPPEDEncapsulate_identification: VarHandle = WSAOVERLAPPEDEncapsulate.varHandle(
		groupElement("identification")
	)
	const val RECEIVE_OPERATION = 1
	const val SEND_OPERATION = 2
	const val CONNECT_OPERATION = 3

	private val associatedSockets = ConcurrentHashMap<Long, WindowsSocketManager>()
	private var centralPortInternal: MemorySegment? = null
		set(value) {
			if (value != null) {
				field = value
				return
			}
			TODO("Destroy threads")
		}

	@get:Synchronized
	private val centralPort: MemorySegment
		get() {
			val internal = centralPortInternal
			if (internal != null) return internal
			val newPort = nativeCreateIoCompletionPort!!.invokeExact(
				capturedStateSegment,
				INVALID_HANDLE_VALUE,
				MemorySegment.NULL,
				0L,
				0
			) as MemorySegment
			if (newPort == MemorySegment.NULL) throwLastError()
			Thread.ofPlatform().daemon(true).name("BSL Socket Manager, for Windows - IOCP manager").start {
				while (true) {
					threadLocalDWORD0.set(DWORD, 0, 0)
					val status = nativeGetQueuedCompletionStatus!!.invokeExact(
						capturedStateSegment,
						newPort,
						threadLocalDWORD0,
						threadLocalULONG_PTR0,
						threadLocalPTR,
						INFINITE
					) as Int
					val overlapped = threadLocalPTR.get(ValueLayout.ADDRESS, 0)
					if (status == 0 && overlapped == MemorySegment.NULL) throwLastError()
					val socketDesc = threadLocalULONG_PTR0.get(ULONG_PTR, 0)
					val socketManager = associatedSockets[socketDesc] ?: continue
					val encapsulate = overlapped.reinterpret(WSAOVERLAPPEDEncapsulate.byteSize())
					val identification = WSAOVERLAPPEDEncapsulate_identification.get(encapsulate, 0L) as Int
					if (status == 0) {
						socketManager.releaseSemaphore(
							identification,
							threadLocalDWORD0.get(DWORD, 0),
							getWin32Error(wsaLastError) ?: IllegalStateException()
						)
						continue
					}
					when (val operation = WSAOVERLAPPEDEncapsulate_operation.get(encapsulate, 0L) as Int) {
						RECEIVE_OPERATION, SEND_OPERATION -> socketManager.releaseSemaphore(
							identification,
							threadLocalDWORD0.get(DWORD, 0)
						)

						CONNECT_OPERATION -> socketManager.releaseSemaphore(
							identification,
							0
						)

						else -> throw IllegalStateException(
							"Unknown socket operation [$operation]"
						)
					}
				}
			}
			centralPortInternal = newPort
			return newPort
		}

	fun addSocket(s: Long): WindowsSocketManager {
		if (associatedSockets.containsKey(s)) throw IllegalArgumentException("Duplicate addition of socket $s")
		val status = nativeCreateIoCompletionPort!!.invokeExact(
			capturedStateSegment,
			MemorySegment.ofAddress(s),
			centralPort,
			s,
			0
		) as MemorySegment
		if (status == MemorySegment.NULL) throwLastError()
		val manager = WindowsSocketManager()
		associatedSockets[s] = manager
		return manager
	}

	fun dropSocket(s: Long) = synchronized(associatedSockets) {
		if (associatedSockets.remove(s) == null) return@synchronized
		if (associatedSockets.isEmpty()) centralPortInternal = null
	}
}