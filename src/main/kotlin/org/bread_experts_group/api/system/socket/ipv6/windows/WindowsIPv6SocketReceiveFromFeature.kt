@file:Suppress("LongLine")

package org.bread_experts_group.api.system.socket.ipv6.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.io.receive.ReceiveSizeData
import org.bread_experts_group.api.system.socket.DeferredOperation
import org.bread_experts_group.api.system.socket.StandardSocketStatus
import org.bread_experts_group.api.system.socket.feature.SocketReceiveFeature
import org.bread_experts_group.api.system.socket.ipv6.InternetProtocolV6AddressPortData
import org.bread_experts_group.api.system.socket.ipv6.receive.IPv6ReceiveDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.receive.IPv6ReceiveFeatureIdentifier
import org.bread_experts_group.api.system.socket.listen.WindowsReceiveFeatures
import org.bread_experts_group.api.system.socket.sys_feature.windows.WindowsSocketEventManager.RECEIVE_OPERATION
import org.bread_experts_group.api.system.socket.sys_feature.windows.WindowsSocketEventManager.WSAOVERLAPPEDEncapsulate
import org.bread_experts_group.api.system.socket.sys_feature.windows.WindowsSocketEventManager.WSAOVERLAPPEDEncapsulate_identification
import org.bread_experts_group.api.system.socket.sys_feature.windows.WindowsSocketEventManager.WSAOVERLAPPEDEncapsulate_operation
import org.bread_experts_group.api.system.socket.sys_feature.windows.WindowsSocketManager
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.*
import org.bread_experts_group.ffi.windows.wsa.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.util.concurrent.TimeUnit

class WindowsIPv6SocketReceiveFromFeature(
	private val socket: Long,
	private val manager: WindowsSocketManager,
	expresses: FeatureExpression<SocketReceiveFeature<IPv6ReceiveFeatureIdentifier, IPv6ReceiveDataIdentifier>>
) : SocketReceiveFeature<IPv6ReceiveFeatureIdentifier, IPv6ReceiveDataIdentifier>(expresses) {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun gatherSegments(
		data: Collection<MemorySegment>,
		vararg features: IPv6ReceiveFeatureIdentifier
	): DeferredOperation<IPv6ReceiveDataIdentifier> {
		val receiveData = mutableListOf<IPv6ReceiveDataIdentifier>()
		val receiveArena = Arena.ofConfined()
		val sender = receiveArena.allocate(sockaddr_in6)
		val (identification, semaphore, value) = manager.getSemaphore()
		try {
			val allocated = receiveArena.allocate(WSABUF, data.size.toLong())
			var currentSegment = allocated
			data.forEach {
				WSABUF_len.set(currentSegment, 0L, it.byteSize().toInt())
				WSABUF_buf.set(currentSegment, 0L, it)
				currentSegment = currentSegment.asSlice(WSABUF.byteSize())
			}
			var flags = 0
			if (features.contains(WindowsReceiveFeatures.PEEK)) {
				flags = flags or 0x2
				receiveData.add(WindowsReceiveFeatures.PEEK)
			}
			threadLocalDWORD1.set(DWORD, 0, flags)
			threadLocalDWORD2.set(DWORD, 0, sender.byteSize().toInt())
			val overlapped = receiveArena.allocate(WSAOVERLAPPEDEncapsulate)
			WSAOVERLAPPEDEncapsulate_operation.set(overlapped, 0L, RECEIVE_OPERATION)
			WSAOVERLAPPEDEncapsulate_identification.set(overlapped, 0L, identification)
			val status = nativeWSARecvFrom!!.invokeExact(
				capturedStateSegment,
				socket,
				allocated,
				data.size,
				MemorySegment.NULL,
				threadLocalDWORD1,
				sender,
				threadLocalDWORD2,
				overlapped,
				MemorySegment.NULL
			) as Int
			if (status == SOCKET_ERROR) {
				if (wsaLastError != WindowsLastError.ERROR_IO_PENDING.id.toInt()) throwLastWSAError()
			}
		} catch (e: Throwable) {
			receiveArena.close()
			manager.releaseSemaphore(identification, null)
			throw e
		}

		return object : DeferredOperation<IPv6ReceiveDataIdentifier> {
			fun prepareData() {
				receiveArena.use { _ ->
					val addrSeg = sockaddr_in6_sin6_addr_Byte.invokeExact(sender, 0L) as MemorySegment
					val addrBytes = ByteArray(addrSeg.byteSize().toInt())
					MemorySegment.copy(
						addrSeg, ValueLayout.JAVA_BYTE, 0,
						addrBytes, 0, addrBytes.size
					)
					val status = nativeWSAHtons!!.invokeExact(
						capturedStateSegment,
						socket,
						sockaddr_in6_sin6_port.get(sender, 0L) as Short,
						threadLocalDWORD0
					) as Int
					if (status != 0) throwLastWSAError()
					receiveData.add(
						InternetProtocolV6AddressPortData(
							addrBytes,
							threadLocalDWORD0.get(ValueLayout.JAVA_SHORT, 0).toUShort()
						)
					)
					receiveData.add(ReceiveSizeData(value.value as Int))
					value.throwable?.let {
						if (it is WindowsLastErrorException) {
							if (it.error.enum == WindowsLastError.ERROR_NETNAME_DELETED) receiveData.add(
								StandardSocketStatus.CONNECTION_CLOSED
							) else throw it
						}
					}
				}
			}

			override fun block(): List<IPv6ReceiveDataIdentifier> {
				semaphore.acquire()
				prepareData()
				return receiveData
			}

			override fun block(time: Long, unit: TimeUnit): List<IPv6ReceiveDataIdentifier> {
				if (!semaphore.tryAcquire(time, unit)) return emptyList()
				prepareData()
				return receiveData
			}
		}
	}
}