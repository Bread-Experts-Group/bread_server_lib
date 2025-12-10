package org.bread_experts_group.api.system.socket.ipv4.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.AF_INET
import org.bread_experts_group.api.system.io.send.SendSizeData
import org.bread_experts_group.api.system.socket.DeferredOperation
import org.bread_experts_group.api.system.socket.StandardSocketStatus
import org.bread_experts_group.api.system.socket.feature.SocketSendFeature
import org.bread_experts_group.api.system.socket.ipv4.InternetProtocolV4AddressPortData
import org.bread_experts_group.api.system.socket.ipv4.send.IPv4SendDataIdentifier
import org.bread_experts_group.api.system.socket.ipv4.send.IPv4SendFeatureIdentifier
import org.bread_experts_group.api.system.socket.system.DeferredSend
import org.bread_experts_group.api.system.socket.system.SocketMonitor
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.ffi.windows.threadLocalDWORD0
import org.bread_experts_group.ffi.windows.throwLastWSAError
import org.bread_experts_group.ffi.windows.wsa.*
import org.bread_experts_group.ffi.windows.wsaLastError
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class WindowsIPv4SocketSendToFeature(
	private val socket: Long,
	private val monitor: SocketMonitor,
	private val checkForAddress: Boolean,
	expresses: FeatureExpression<SocketSendFeature<IPv4SendFeatureIdentifier, IPv4SendDataIdentifier>>
) : SocketSendFeature<IPv4SendFeatureIdentifier, IPv4SendDataIdentifier>(expresses) {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun scatterSegments(
		data: Collection<MemorySegment>,
		vararg features: IPv4SendFeatureIdentifier
	): DeferredOperation<IPv4SendDataIdentifier> =
		object : DeferredSend<IPv4SendDataIdentifier>(monitor) {
			override fun send(): List<IPv4SendDataIdentifier> = Arena.ofConfined().use { tempArena ->
				val supportedFeatures = mutableListOf<IPv4SendDataIdentifier>()
				val allocated = tempArena.allocate(WSABUF, data.size.toLong())
				var currentSegment = allocated
				data.forEach {
					WSABUF_len.set(currentSegment, 0L, it.byteSize().toInt())
					WSABUF_buf.set(currentSegment, 0L, it)
					currentSegment = currentSegment.asSlice(WSABUF.byteSize())
				}
				if (checkForAddress) {
					val address = features.firstNotNullOfOrNull { it as? InternetProtocolV4AddressPortData }
					if (address != null) {
						val addressSockAddr = tempArena.allocate(sockaddr_in)
						sockaddr_in_sin_family.set(addressSockAddr, 0L, AF_INET.toShort())
						sockaddr_in_sin_port.set(addressSockAddr, 0L, address.port.toShort())
						MemorySegment.copy(
							address.data, 0,
							sockaddr_in_sin_addr.invokeExact(addressSockAddr, 0L) as MemorySegment,
							ValueLayout.JAVA_BYTE, 0,
							address.data.size
						)
						threadLocalDWORD0.set(DWORD, 0, 0)
						val status = nativeWSASendTo!!.invokeExact(
							capturedStateSegment,
							socket,
							allocated,
							data.size,
							threadLocalDWORD0,
							0, // TODO FLAGS
							addressSockAddr,
							addressSockAddr.byteSize().toInt(),
							MemorySegment.NULL,
							MemorySegment.NULL
						) as Int
						if (status != 0) {
							when (wsaLastError) {
								10054 -> supportedFeatures.add(StandardSocketStatus.CONNECTION_CLOSED)
								10035 -> {}
								else -> throwLastWSAError()
							}
						} else if (monitor.write.availablePermits() < 1) monitor.write.release()
						supportedFeatures.add(SendSizeData(threadLocalDWORD0.get(DWORD, 0)))
						supportedFeatures.add(address)
						return supportedFeatures
					}
				}

				threadLocalDWORD0.set(DWORD, 0, 0)
				val status = nativeWSASend!!.invokeExact(
					capturedStateSegment,
					socket,
					allocated,
					data.size,
					threadLocalDWORD0,
					0, // TODO FLAGS
					MemorySegment.NULL,
					MemorySegment.NULL
				) as Int
				if (status != 0) {
					when (wsaLastError) {
						10054 -> supportedFeatures.add(StandardSocketStatus.CONNECTION_CLOSED)
						10035 -> {}
						else -> throwLastWSAError()
					}
				} else if (monitor.write.availablePermits() < 1) monitor.write.release()
				supportedFeatures.add(SendSizeData(threadLocalDWORD0.get(DWORD, 0)))
				return supportedFeatures
			}
		}
}