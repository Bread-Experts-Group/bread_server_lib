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

class WindowsIPv6SocketSendToFeature(
	private val socket: Long,
	private val monitor: SocketMonitor,
	private val checkForAddress: Boolean,
	expresses: FeatureExpression<SocketSendFeature<IPv6SendFeatureIdentifier, IPv6SendDataIdentifier>>
) : SocketSendFeature<IPv6SendFeatureIdentifier, IPv6SendDataIdentifier>(expresses) {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun scatterSegments(
		data: Collection<MemorySegment>,
		vararg features: IPv6SendFeatureIdentifier
	): DeferredOperation<IPv6SendDataIdentifier> =
		object : DeferredSend<IPv6SendDataIdentifier>(monitor) {
			override fun send(): List<IPv6SendDataIdentifier> = Arena.ofConfined().use { tempArena ->
				val supportedFeatures = mutableListOf<IPv6SendDataIdentifier>()
				val allocated = tempArena.allocate(WSABUF, data.size.toLong())
				var currentSegment = allocated
				data.forEach {
					WSABUF_len.set(currentSegment, 0L, it.byteSize().toInt())
					WSABUF_buf.set(currentSegment, 0L, it)
					currentSegment = currentSegment.asSlice(WSABUF.byteSize())
				}
				if (checkForAddress) {
					val address = features.firstNotNullOfOrNull { it as? InternetProtocolV6AddressPortData }
					if (address != null) {
						val addressSockAddr = tempArena.allocate(sockaddr_in6)
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
						threadLocalDWORD0.set(DWORD, 0, 0)
						status = nativeWSASendTo!!.invokeExact(
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
								WSAECONNRESET -> supportedFeatures.add(StandardSocketStatus.CONNECTION_CLOSED)
								WSAEWOULDBLOCK -> {}
								else -> throwLastWSAError()
							}
						} else monitor.write.release()
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
						WSAECONNRESET -> supportedFeatures.add(StandardSocketStatus.CONNECTION_CLOSED)
						WSAEWOULDBLOCK -> {}
						else -> throwLastWSAError()
					}
				} else monitor.write.release()
				supportedFeatures.add(SendSizeData(threadLocalDWORD0.get(DWORD, 0)))
				return supportedFeatures
			}
		}
}