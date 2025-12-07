package org.bread_experts_group.api.system.socket.ipv6.posix

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.socket.DeferredSocketOperation
import org.bread_experts_group.api.system.socket.close.SocketCloseFeatureIdentifier
import org.bread_experts_group.api.system.socket.feature.SocketAcceptFeature
import org.bread_experts_group.api.system.socket.feature.SocketFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv6.IPv6Socket
import org.bread_experts_group.api.system.socket.ipv6.IPv6SocketFeatures
import org.bread_experts_group.api.system.socket.ipv6.InternetProtocolV6AddressPortData
import org.bread_experts_group.api.system.socket.ipv6.accept.IPv6AcceptDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.accept.IPv6AcceptFeatureIdentifier
import org.bread_experts_group.api.system.socket.system.DeferredSocketAccept
import org.bread_experts_group.api.system.socket.system.SocketMonitor
import org.bread_experts_group.api.system.socket.system.linux.LinuxSocketEventManager
import org.bread_experts_group.api.system.socket.system.linux.linuxClose
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.posix.*
import org.bread_experts_group.ffi.threadLocalInt
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class LinuxIPv6SocketAcceptFeature(
	private val socket: Int,
	private val monitor: SocketMonitor,
	expresses: FeatureExpression<SocketAcceptFeature<IPv6AcceptFeatureIdentifier, IPv6AcceptDataIdentifier>>
) : SocketAcceptFeature<IPv6AcceptFeatureIdentifier, IPv6AcceptDataIdentifier>(expresses) {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun accept(
		vararg features: IPv6AcceptFeatureIdentifier
	): DeferredSocketOperation<IPv6AcceptDataIdentifier> =
		object : DeferredSocketAccept<IPv6AcceptDataIdentifier>(monitor) {
			override fun accept(): List<IPv6AcceptDataIdentifier> = Arena.ofConfined().use { tempArena ->
				val sockAddr = tempArena.allocate(sockaddr_in6)
				threadLocalInt.set(ValueLayout.JAVA_INT, 0, sockAddr.byteSize().toInt())
				val acceptedSocket = nativeAccept!!.invokeExact(
					capturedStateSegment,
					socket,
					sockAddr,
					threadLocalInt
				) as Int
				if (acceptedSocket == -1) throwLastErrno()
				val acceptedMonitor = LinuxSocketEventManager.addSocket(acceptedSocket)
				acceptedMonitor.forAccept = false
				acceptedMonitor.wakeup()
				val addrSeg = sockaddr_in6_sin6_addr.invokeExact(sockAddr, 0L) as MemorySegment
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
							POSIXIPv6SocketSendToFeature(
								acceptedSocket, acceptedMonitor, false,
								IPv6SocketFeatures.SEND
							),
							POSIXIPv6SocketReceiveFeature(
								acceptedSocket, acceptedMonitor,
								IPv6SocketFeatures.RECEIVE
							)
						)

						override fun close(
							vararg features: SocketCloseFeatureIdentifier
						) = linuxClose(acceptedSocket, *features)
					}
				)
			}
		}
}