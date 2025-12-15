package org.bread_experts_group.api.system.socket.ipv6.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.socket.DeferredOperation
import org.bread_experts_group.api.system.socket.close.SocketCloseFeatureIdentifier
import org.bread_experts_group.api.system.socket.feature.SocketAcceptFeature
import org.bread_experts_group.api.system.socket.feature.SocketFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv6.IPv6Socket
import org.bread_experts_group.api.system.socket.ipv6.IPv6SocketFeatures
import org.bread_experts_group.api.system.socket.ipv6.InternetProtocolV6AddressPortData
import org.bread_experts_group.api.system.socket.ipv6.accept.IPv6AcceptDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.accept.IPv6AcceptFeatureIdentifier
import org.bread_experts_group.api.system.socket.system.DeferredAccept
import org.bread_experts_group.api.system.socket.system.SocketMonitor
import org.bread_experts_group.api.system.socket.system.windows.WindowsSocketEventManager
import org.bread_experts_group.api.system.socket.system.windows.winClose
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.ffi.windows.threadLocalDWORD0
import org.bread_experts_group.ffi.windows.throwLastWSAError
import org.bread_experts_group.ffi.windows.wsa.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class WindowsIPv6SocketAcceptFeature(
	private val socket: Long,
	private val monitor: SocketMonitor,
	expresses: FeatureExpression<SocketAcceptFeature<IPv6AcceptFeatureIdentifier, IPv6AcceptDataIdentifier>>
) : SocketAcceptFeature<IPv6AcceptFeatureIdentifier, IPv6AcceptDataIdentifier>(expresses) {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun accept(
		vararg features: IPv6AcceptFeatureIdentifier
	): DeferredOperation<IPv6AcceptDataIdentifier> =
		object : DeferredAccept<IPv6AcceptDataIdentifier>(monitor) {
			override fun accept(): List<IPv6AcceptDataIdentifier> = Arena.ofConfined().use { tempArena ->
				val sockAddr = tempArena.allocate(sockaddr_in6)
				threadLocalDWORD0.set(DWORD, 0, sockAddr.byteSize().toInt())
				val acceptedSocket = nativeWSAAccept!!.invokeExact(
					capturedStateSegment,
					socket,
					sockAddr,
					threadLocalDWORD0,
					MemorySegment.NULL,
					0L
				) as Long
				if (acceptedSocket == INVALID_SOCKET) throwLastWSAError()
				val acceptedMonitor = WindowsSocketEventManager.addSocket(acceptedSocket)
				val addrSeg = sockaddr_in6_sin6_addr_Byte.invokeExact(sockAddr, 0L) as MemorySegment
				val addrBytes = ByteArray(addrSeg.byteSize().toInt())
				MemorySegment.copy(
					addrSeg, ValueLayout.JAVA_BYTE, 0,
					addrBytes, 0, addrBytes.size
				)
				listOf(
					InternetProtocolV6AddressPortData(
						addrBytes,
						(sockaddr_in6_sin6_port.get(sockAddr, 0L) as Short).toUShort()
					),
					object : IPv6Socket() {
						override val features: MutableList<SocketFeatureImplementation<*>> = mutableListOf(
							WindowsIPv6SocketSendToFeature(
								acceptedSocket, acceptedMonitor, false,
								IPv6SocketFeatures.SEND
							),
							WindowsIPv6SocketReceiveFeature(
								acceptedSocket, acceptedMonitor,
								IPv6SocketFeatures.RECEIVE
							)
						)

						override fun close(
							vararg features: SocketCloseFeatureIdentifier
						) = winClose(acceptedSocket, *features)
					}
				)
			}
		}
}