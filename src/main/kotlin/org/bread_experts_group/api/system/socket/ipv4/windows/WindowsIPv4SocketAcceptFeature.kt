package org.bread_experts_group.api.system.socket.ipv4.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.socket.DeferredOperation
import org.bread_experts_group.api.system.socket.close.SocketCloseFeatureIdentifier
import org.bread_experts_group.api.system.socket.feature.SocketAcceptFeature
import org.bread_experts_group.api.system.socket.feature.SocketFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv4.IPv4Socket
import org.bread_experts_group.api.system.socket.ipv4.IPv4SocketFeatures
import org.bread_experts_group.api.system.socket.ipv4.InternetProtocolV4AddressPortData
import org.bread_experts_group.api.system.socket.ipv4.accept.IPv4AcceptDataIdentifier
import org.bread_experts_group.api.system.socket.ipv4.accept.IPv4AcceptFeatureIdentifier
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

class WindowsIPv4SocketAcceptFeature(
	private val socket: Long,
	private val monitor: SocketMonitor,
	expresses: FeatureExpression<SocketAcceptFeature<IPv4AcceptFeatureIdentifier, IPv4AcceptDataIdentifier>>
) : SocketAcceptFeature<IPv4AcceptFeatureIdentifier, IPv4AcceptDataIdentifier>(expresses) {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun accept(
		vararg features: IPv4AcceptFeatureIdentifier
	): DeferredOperation<IPv4AcceptDataIdentifier> =
		object : DeferredAccept<IPv4AcceptDataIdentifier>(monitor) {
			override fun accept(): List<IPv4AcceptDataIdentifier> = Arena.ofConfined().use { tempArena ->
				val sockAddr = tempArena.allocate(sockaddr_in)
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
				val addrSeg = sockaddr_in_sin_addr.invokeExact(sockAddr, 0L) as MemorySegment
				val addrBytes = ByteArray(addrSeg.byteSize().toInt())
				MemorySegment.copy(
					addrSeg, ValueLayout.JAVA_BYTE, 0,
					addrBytes, 0, addrBytes.size
				)
				listOf(
					InternetProtocolV4AddressPortData(
						addrBytes,
						(sockaddr_in_sin_port.get(sockAddr, 0L) as Short).toUShort()
					),
					object : IPv4Socket() {
						override val features: MutableList<SocketFeatureImplementation<*>> = mutableListOf(
							WindowsIPv4SocketSendToFeature(
								acceptedSocket, acceptedMonitor, false,
								IPv4SocketFeatures.SEND
							),
							WindowsIPv4SocketReceiveFeature(
								acceptedSocket, acceptedMonitor,
								IPv4SocketFeatures.RECEIVE
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