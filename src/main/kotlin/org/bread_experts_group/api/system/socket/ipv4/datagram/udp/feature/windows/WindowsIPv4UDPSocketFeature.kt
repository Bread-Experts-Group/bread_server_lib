package org.bread_experts_group.api.system.socket.ipv4.datagram.udp.feature.windows

import org.bread_experts_group.api.feature.CheckedImplementation
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.AF_INET
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.IPPROTO_UDP
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.SOCK_DGRAM
import org.bread_experts_group.api.system.socket.close.SocketCloseFeatureIdentifier
import org.bread_experts_group.api.system.socket.feature.SocketFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv4.IPv4Socket
import org.bread_experts_group.api.system.socket.ipv4.IPv4SocketFeatures
import org.bread_experts_group.api.system.socket.ipv4.datagram.udp.feature.IPv4UDPSocketFeature
import org.bread_experts_group.api.system.socket.ipv4.windows.WindowsIPv4SocketBindFeature
import org.bread_experts_group.api.system.socket.ipv4.windows.WindowsIPv4SocketReceiveFromFeature
import org.bread_experts_group.api.system.socket.ipv4.windows.WindowsIPv4SocketSendToFeature
import org.bread_experts_group.api.system.socket.system.windows.WindowsSocketEventManager
import org.bread_experts_group.api.system.socket.system.windows.winClose
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.throwLastWSAError
import org.bread_experts_group.ffi.windows.wsa.INVALID_SOCKET
import org.bread_experts_group.ffi.windows.wsa.nativeSocket

class WindowsIPv4UDPSocketFeature : IPv4UDPSocketFeature(), CheckedImplementation {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeSocket != null

	override fun openSocket(): IPv4Socket {
		val socket = nativeSocket!!.invokeExact(
			capturedStateSegment,
			AF_INET, SOCK_DGRAM, IPPROTO_UDP
		) as Long
		if (socket == INVALID_SOCKET) throwLastWSAError()
		val monitor = WindowsSocketEventManager.addSocket(socket)
		return object : IPv4Socket() {
			override val features: MutableList<SocketFeatureImplementation<*>> = mutableListOf(
				WindowsIPv4UDPConnectFeature(socket, monitor, IPv4SocketFeatures.CONNECT),
				WindowsIPv4SocketSendToFeature(socket, monitor, true, IPv4SocketFeatures.SEND),
				WindowsIPv4SocketReceiveFromFeature(socket, monitor, IPv4SocketFeatures.RECEIVE),
				WindowsIPv4SocketBindFeature(socket, IPv4SocketFeatures.BIND)
			)


			override fun close(
				vararg features: SocketCloseFeatureIdentifier
			) = winClose(socket, *features)
		}
	}
}