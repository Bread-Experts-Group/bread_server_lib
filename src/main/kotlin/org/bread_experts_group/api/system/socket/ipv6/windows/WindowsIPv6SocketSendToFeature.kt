@file:Suppress("LongLine")

package org.bread_experts_group.api.system.socket.ipv6.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.AF_INET6
import org.bread_experts_group.api.system.io.send.SendSizeData
import org.bread_experts_group.api.system.socket.DeferredOperation
import org.bread_experts_group.api.system.socket.StandardSocketStatus
import org.bread_experts_group.api.system.socket.feature.SocketSendFeature
import org.bread_experts_group.api.system.socket.ipv6.InternetProtocolV6AddressPortData
import org.bread_experts_group.api.system.socket.ipv6.send.IPv6SendDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.send.IPv6SendFeatureIdentifier
import org.bread_experts_group.api.system.socket.sys_feature.windows.WindowsSocketEventManager.SEND_OPERATION
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

class WindowsIPv6SocketSendToFeature(
	private val socket: Long,
	private val manager: WindowsSocketManager,
	private val checkForAddress: Boolean,
	expresses: FeatureExpression<SocketSendFeature<IPv6SendFeatureIdentifier, IPv6SendDataIdentifier>>
) : SocketSendFeature<IPv6SendFeatureIdentifier, IPv6SendDataIdentifier>(expresses) {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun scatterSegments(
		data: Collection<MemorySegment>,
		vararg features: IPv6SendFeatureIdentifier
	): DeferredOperation<IPv6SendDataIdentifier> {
		val sendData = mutableListOf<IPv6SendDataIdentifier>()
		val sendArena = Arena.ofConfined()
		val (identification, semaphore, value) = manager.getSemaphore()
		try {
			val allocated = sendArena.allocate(WSABUF, data.size.toLong())
			var currentSegment = allocated
			data.forEach {
				WSABUF_len.set(currentSegment, 0L, it.byteSize().toInt())
				WSABUF_buf.set(currentSegment, 0L, it)
				currentSegment = currentSegment.asSlice(WSABUF.byteSize())
			}
			val overlapped = sendArena.allocate(WSAOVERLAPPEDEncapsulate)
			WSAOVERLAPPEDEncapsulate_operation.set(overlapped, 0L, SEND_OPERATION)
			WSAOVERLAPPEDEncapsulate_identification.set(overlapped, 0L, identification)
			var addressClear = true
			if (checkForAddress) {
				val address = features.firstNotNullOfOrNull { it as? InternetProtocolV6AddressPortData }
				if (address != null) {
					addressClear = false
					sendData.add(address)
					val addressSockAddr = sendArena.allocate(sockaddr_in6)
					sockaddr_in6_sin6_family.set(addressSockAddr, 0L, AF_INET6.toShort())
					var status = nativeWSAHtons!!.invokeExact(
						capturedStateSegment,
						socket,
						address.port.toShort(),
						threadLocalDWORD0
					) as Int
					if (status != 0) throwLastWSAError()
					sockaddr_in6_sin6_port.set(
						addressSockAddr, 0L,
						threadLocalDWORD0.get(ValueLayout.JAVA_SHORT, 0)
					)
					MemorySegment.copy(
						address.data, 0,
						sockaddr_in6_sin6_addr_Byte.invokeExact(addressSockAddr, 0L) as MemorySegment,
						ValueLayout.JAVA_BYTE, 0,
						address.data.size
					)
					status = nativeWSASendTo!!.invokeExact(
						capturedStateSegment,
						socket,
						allocated,
						data.size,
						MemorySegment.NULL,
						0, // TODO FLAGS
						addressSockAddr,
						addressSockAddr.byteSize().toInt(),
						overlapped,
						MemorySegment.NULL
					) as Int
					if (status != 0) {
						if (wsaLastError != WindowsLastError.ERROR_IO_PENDING.id.toInt()) throwLastWSAError()
					}
				}
			}
			if (addressClear) {
				val status = nativeWSASend!!.invokeExact(
					capturedStateSegment,
					socket,
					allocated,
					data.size,
					MemorySegment.NULL,
					0, // TODO FLAGS
					overlapped,
					MemorySegment.NULL
				) as Int
				if (status != 0) {
					if (wsaLastError != WindowsLastError.ERROR_IO_PENDING.id.toInt()) throwLastWSAError()
				}
			}
		} catch (e: Throwable) {
			sendArena.close()
			manager.releaseSemaphore(identification, null)
			throw e
		}

		return object : DeferredOperation<IPv6SendDataIdentifier> {
			fun prepareData() {
				sendArena.use { _ ->
					sendData.add(SendSizeData(value.value as Int))
					value.throwable?.let {
						if (it is WindowsLastErrorException) {
							if (it.error.enum == WindowsLastError.ERROR_NETNAME_DELETED) sendData.add(
								StandardSocketStatus.CONNECTION_CLOSED
							) else throw it
						}
					}
				}
			}

			override fun block(): List<IPv6SendDataIdentifier> {
				semaphore.acquire()
				prepareData()
				return sendData
			}

			override fun block(time: Long, unit: TimeUnit): List<IPv6SendDataIdentifier> {
				if (!semaphore.tryAcquire(time, unit)) return emptyList()
				prepareData()
				return sendData
			}
		}
	}
}