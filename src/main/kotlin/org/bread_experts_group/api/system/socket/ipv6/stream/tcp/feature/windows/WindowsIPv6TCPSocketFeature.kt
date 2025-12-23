package org.bread_experts_group.api.system.socket.ipv6.stream.tcp.feature.windows

import org.bread_experts_group.api.feature.CheckedImplementation
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.AF_INET6
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.IPPROTO_TCP
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.SOCK_STREAM
import org.bread_experts_group.api.system.socket.close.SocketCloseFeatureIdentifier
import org.bread_experts_group.api.system.socket.feature.SocketFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv6.IPv6Socket
import org.bread_experts_group.api.system.socket.ipv6.IPv6SocketFeatures
import org.bread_experts_group.api.system.socket.ipv6.stream.tcp.feature.IPv6TCPSocketFeature
import org.bread_experts_group.api.system.socket.ipv6.windows.*
import org.bread_experts_group.api.system.socket.sys_feature.windows.WindowsSocketEventManager
import org.bread_experts_group.api.system.socket.sys_feature.windows.winClose
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.throwLastWSAError
import org.bread_experts_group.ffi.windows.wsa.INVALID_SOCKET
import org.bread_experts_group.ffi.windows.wsa.nativeWSASocketWide
import java.lang.foreign.MemorySegment

class WindowsIPv6TCPSocketFeature : IPv6TCPSocketFeature(), CheckedImplementation {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeWSASocketWide != null

	override fun openSocket(): IPv6Socket {
		val socket = nativeWSASocketWide!!.invokeExact(
			capturedStateSegment,
			AF_INET6, SOCK_STREAM, IPPROTO_TCP,
			MemorySegment.NULL, 0, 0x01
		) as Long
		if (socket == INVALID_SOCKET) throwLastWSAError()
		val manager = WindowsSocketEventManager.addSocket(socket)
		return object : IPv6Socket() {
			override val features: MutableList<SocketFeatureImplementation<*>> = mutableListOf(
				WindowsIPv6TCPConnectFeature(socket, manager, IPv6SocketFeatures.CONNECT),
				WindowsIPv6SocketSendToFeature(socket, manager, false, IPv6SocketFeatures.SEND),
				WindowsIPv6SocketReceiveFeature(socket, manager, IPv6SocketFeatures.RECEIVE),
				WindowsIPv6SocketBindFeature(socket, IPv6SocketFeatures.BIND),
				WindowsIPv6SocketListenFeature(socket, IPv6SocketFeatures.LISTEN),
				WindowsIPv6SocketAcceptFeature(socket, manager, IPv6SocketFeatures.ACCEPT),
				WindowsIPv6SocketConfigureFeature(socket, IPv6SocketFeatures.CONFIGURE)
			)

			override fun close(
				vararg features: SocketCloseFeatureIdentifier
			) = winClose(socket, *features)
		}
	}
}