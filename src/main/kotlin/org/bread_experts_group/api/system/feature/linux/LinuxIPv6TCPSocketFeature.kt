package org.bread_experts_group.api.system.feature.linux

import org.bread_experts_group.api.feature.CheckedImplementation
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.feature.linux.LinuxSystemNetworkingSocketsFeature.Companion.AF_INET6
import org.bread_experts_group.api.system.feature.linux.LinuxSystemNetworkingSocketsFeature.Companion.IPPROTO_TCP
import org.bread_experts_group.api.system.feature.linux.LinuxSystemNetworkingSocketsFeature.Companion.SOCK_STREAM
import org.bread_experts_group.api.system.socket.close.SocketCloseFeatureIdentifier
import org.bread_experts_group.api.system.socket.feature.SocketFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv6.IPv6Socket
import org.bread_experts_group.api.system.socket.ipv6.IPv6SocketFeatures
import org.bread_experts_group.api.system.socket.ipv6.posix.*
import org.bread_experts_group.api.system.socket.ipv6.stream.tcp.feature.IPv6TCPSocketFeature
import org.bread_experts_group.api.system.socket.system.linux.LinuxSocketEventManager
import org.bread_experts_group.api.system.socket.system.linux.linuxClose
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.posix.nativeSocket
import org.bread_experts_group.ffi.posix.throwLastErrno

class LinuxIPv6TCPSocketFeature : IPv6TCPSocketFeature(), CheckedImplementation {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeSocket != null

	override fun openSocket(): IPv6Socket {
		val socket = nativeSocket!!.invokeExact(
			capturedStateSegment,
			AF_INET6, SOCK_STREAM, IPPROTO_TCP
		) as Int
		if (socket == -1) throwLastErrno()
		val monitor = LinuxSocketEventManager.addSocket(socket)
		return object : IPv6Socket() {
			override val features: MutableList<SocketFeatureImplementation<*>> = mutableListOf(
//				WindowsIPv6TCPConnectFeature(socket, monitor, IPv6SocketFeatures.CONNECT), // TODO connect
				POSIXIPv6SocketSendToFeature(socket, monitor, false, IPv6SocketFeatures.SEND),
				POSIXIPv6SocketReceiveFeature(socket, monitor, IPv6SocketFeatures.RECEIVE),
				POSIXIPv6SocketBindFeature(socket, IPv6SocketFeatures.BIND),
				LinuxIPv6SocketListenFeature(socket, monitor, IPv6SocketFeatures.LISTEN),
				LinuxIPv6SocketAcceptFeature(socket, monitor, IPv6SocketFeatures.ACCEPT),
				POSIXIPv6SocketConfigureFeature(socket, IPv6SocketFeatures.CONFIGURE)
			)

			override fun close(
				vararg features: SocketCloseFeatureIdentifier
			) = linuxClose(socket, *features)
		}
	}
}