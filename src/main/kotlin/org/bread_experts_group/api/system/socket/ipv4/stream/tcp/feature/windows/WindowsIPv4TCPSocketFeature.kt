package org.bread_experts_group.api.system.socket.ipv4.stream.tcp.feature.windows

import org.bread_experts_group.api.feature.CheckedImplementation
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.AF_INET
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.IPPROTO_TCP
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.SOCK_STREAM
import org.bread_experts_group.api.system.socket.close.SocketCloseFeatureIdentifier
import org.bread_experts_group.api.system.socket.feature.SocketFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv4.IPv4Socket
import org.bread_experts_group.api.system.socket.ipv4.IPv4SocketFeatures
import org.bread_experts_group.api.system.socket.ipv4.stream.tcp.feature.IPv4TCPSocketFeature
import org.bread_experts_group.api.system.socket.ipv4.windows.*
import org.bread_experts_group.api.system.socket.system.windows.WindowsSocketEventManager
import org.bread_experts_group.api.system.socket.system.windows.winClose
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.throwLastWSAError
import org.bread_experts_group.ffi.windows.wsa.INVALID_SOCKET
import org.bread_experts_group.ffi.windows.wsa.nativeSocket

class WindowsIPv4TCPSocketFeature : IPv4TCPSocketFeature(), CheckedImplementation {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeSocket != null

	override fun openSocket(): IPv4Socket {
		val socket = nativeSocket!!.invokeExact(
			capturedStateSegment,
			AF_INET, SOCK_STREAM, IPPROTO_TCP
		) as Long
		val monitor = WindowsSocketEventManager.addSocket(socket)
		if (socket == INVALID_SOCKET) throwLastWSAError()
		return object : IPv4Socket() {
			override val features: MutableList<SocketFeatureImplementation<*>> = mutableListOf(
				WindowsIPv4TCPConnectFeature(socket, monitor, IPv4SocketFeatures.CONNECT),
				WindowsIPv4SocketSendToFeature(socket, monitor, false, IPv4SocketFeatures.SEND),
				WindowsIPv4SocketReceiveFeature(socket, monitor, IPv4SocketFeatures.RECEIVE),
				WindowsIPv4SocketBindFeature(socket, IPv4SocketFeatures.BIND),
				WindowsIPv4SocketListenFeature(socket, IPv4SocketFeatures.LISTEN),
				WindowsIPv4SocketAcceptFeature(socket, monitor, IPv4SocketFeatures.ACCEPT)
			)

			override fun close(
				vararg features: SocketCloseFeatureIdentifier
			) = winClose(socket, *features)
		}
	}
}