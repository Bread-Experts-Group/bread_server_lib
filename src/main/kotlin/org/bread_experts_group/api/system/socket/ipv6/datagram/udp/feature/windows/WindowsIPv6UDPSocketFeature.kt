package org.bread_experts_group.api.system.socket.ipv6.datagram.udp.feature.windows

import org.bread_experts_group.api.feature.CheckedImplementation
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.AF_INET6
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.IPPROTO_UDP
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.SOCK_DGRAM
import org.bread_experts_group.api.system.socket.close.SocketCloseFeatureIdentifier
import org.bread_experts_group.api.system.socket.feature.SocketFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv4.windows.winClose
import org.bread_experts_group.api.system.socket.ipv6.IPv6Socket
import org.bread_experts_group.api.system.socket.ipv6.IPv6SocketFeatures
import org.bread_experts_group.api.system.socket.ipv6.datagram.udp.feature.IPv6UDPSocketFeature
import org.bread_experts_group.api.system.socket.ipv6.windows.WindowsIPv6SocketBindFeature
import org.bread_experts_group.api.system.socket.ipv6.windows.WindowsIPv6SocketConfigureFeature
import org.bread_experts_group.api.system.socket.ipv6.windows.WindowsIPv6SocketReceiveFromFeature
import org.bread_experts_group.api.system.socket.ipv6.windows.WindowsIPv6SocketSendToFeature
import org.bread_experts_group.api.system.socket.windows.WindowsSocketEventManager
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.throwLastWSAError
import org.bread_experts_group.ffi.windows.wsa.INVALID_SOCKET
import org.bread_experts_group.ffi.windows.wsa.nativeSocket

class WindowsIPv6UDPSocketFeature : IPv6UDPSocketFeature(), CheckedImplementation {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeSocket != null

	override fun openSocket(): IPv6Socket {
		val socket = nativeSocket!!.invokeExact(
			capturedStateSegment,
			AF_INET6, SOCK_DGRAM, IPPROTO_UDP
		) as Long
		if (socket == INVALID_SOCKET) throwLastWSAError()
		val monitor = WindowsSocketEventManager.addSocket(socket)
		return object : IPv6Socket() {
			override val features: MutableList<SocketFeatureImplementation<*>> = mutableListOf(
				WindowsIPv6UDPConnectFeature(socket, monitor, IPv6SocketFeatures.CONNECT),
				WindowsIPv6SocketSendToFeature(socket, monitor, IPv6SocketFeatures.SEND),
				WindowsIPv6SocketReceiveFromFeature(socket, monitor, IPv6SocketFeatures.RECEIVE),
				WindowsIPv6SocketBindFeature(socket, IPv6SocketFeatures.BIND),
				WindowsIPv6SocketConfigureFeature(socket, IPv6SocketFeatures.CONFIGURE)
			)

			override fun close(
				vararg features: SocketCloseFeatureIdentifier
			) = winClose(socket, *features)
		}
	}
}