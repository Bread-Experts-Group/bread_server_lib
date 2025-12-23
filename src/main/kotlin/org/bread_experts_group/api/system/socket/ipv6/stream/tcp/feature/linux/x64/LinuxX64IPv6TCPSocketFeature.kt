package org.bread_experts_group.api.system.socket.ipv6.stream.tcp.feature.linux.x64

import org.bread_experts_group.api.feature.CheckedImplementation
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.socket.close.SocketCloseFeatureIdentifier
import org.bread_experts_group.api.system.socket.feature.SocketFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv6.IPv6Socket
import org.bread_experts_group.api.system.socket.ipv6.stream.tcp.feature.IPv6TCPSocketFeature
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.posix.linux.x64.AF_INET6
import org.bread_experts_group.ffi.posix.linux.x64.IPPROTO_TCP
import org.bread_experts_group.ffi.posix.linux.x64.SOCK_STREAM
import org.bread_experts_group.ffi.posix.linux.x64.nativeSocket
import org.bread_experts_group.ffi.posix.x64.throwLastErrno

class LinuxX64IPv6TCPSocketFeature : IPv6TCPSocketFeature(), CheckedImplementation {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeSocket != null

	override fun openSocket(): IPv6Socket {
		val socket = nativeSocket!!.invokeExact(
			capturedStateSegment,
			AF_INET6, SOCK_STREAM, IPPROTO_TCP
		) as Int
		if (socket == -1) throwLastErrno()
//		val manager = WindowsSocketEventManager.addSocket(socket)
		return object : IPv6Socket() {
			override val features: MutableList<SocketFeatureImplementation<*>> = mutableListOf(
//				WindowsIPv6TCPConnectFeature(socket, manager, IPv6SocketFeatures.CONNECT),
//				WindowsIPv6SocketSendToFeature(socket, manager, false, IPv6SocketFeatures.SEND),
//				WindowsIPv6SocketReceiveFeature(socket, manager, IPv6SocketFeatures.RECEIVE),
//				WindowsIPv6SocketBindFeature(socket, IPv6SocketFeatures.BIND),
//				WindowsIPv6SocketListenFeature(socket, IPv6SocketFeatures.LISTEN),
//				WindowsIPv6SocketAcceptFeature(socket, manager, IPv6SocketFeatures.ACCEPT),
//				WindowsIPv6SocketConfigureFeature(socket, IPv6SocketFeatures.CONFIGURE)
			)

			override fun close(
				vararg features: SocketCloseFeatureIdentifier
			) = TODO("!") //winClose(socket, *features)
		}
	}
}