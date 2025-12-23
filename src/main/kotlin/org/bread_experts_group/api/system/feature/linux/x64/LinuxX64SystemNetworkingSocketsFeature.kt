package org.bread_experts_group.api.system.feature.linux.x64

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.feature.SystemNetworkingSocketsFeature
import org.bread_experts_group.api.system.socket.ipv6.datagram.feature.SystemInternetProtocolV6DatagramProtocolFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv6.datagram.feature.SystemInternetProtocolV6UDPFeature
import org.bread_experts_group.api.system.socket.ipv6.datagram.udp.feature.IPv6UDPFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv6.datagram.udp.feature.linux.x64.LinuxX64IPv6UDPResolutionFeature
import org.bread_experts_group.api.system.socket.ipv6.datagram.udp.feature.linux.x64.LinuxX64IPv6UDPSocketFeature
import org.bread_experts_group.api.system.socket.ipv6.feature.SystemInternetProtocolV6DatagramProtocolsSocketProviderFeature
import org.bread_experts_group.api.system.socket.ipv6.feature.SystemInternetProtocolV6SocketProviderFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv6.feature.SystemInternetProtocolV6StreamProtocolsSocketProviderFeature
import org.bread_experts_group.api.system.socket.ipv6.stream.feature.SystemInternetProtocolV6StreamProtocolFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv6.stream.feature.SystemInternetProtocolV6TCPFeature
import org.bread_experts_group.api.system.socket.ipv6.stream.tcp.feature.IPv6TCPFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv6.stream.tcp.feature.linux.x64.LinuxX64IPv6TCPResolutionFeature
import org.bread_experts_group.api.system.socket.ipv6.stream.tcp.feature.linux.x64.LinuxX64IPv6TCPSocketFeature
import org.bread_experts_group.api.system.socket.sys_feature.SystemSocketProviderFeatureImplementation
import org.bread_experts_group.api.system.socket.sys_feature.SystemSocketProviderInternetProtocolV6Feature
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.posix.linux.x64.*
import org.bread_experts_group.ffi.posix.x64.throwLastErrno

class LinuxX64SystemNetworkingSocketsFeature : SystemNetworkingSocketsFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override val features: MutableList<SystemSocketProviderFeatureImplementation<*>> by lazy {
		val list = mutableListOf<SystemSocketProviderFeatureImplementation<*>>()
		fun testSocket(domain: Int, type: Int, protocol: Int): Boolean {
			val opened = nativeSocket!!.invokeExact(capturedStateSegment, domain, type, protocol) as Int
			if (opened == -1) throwLastErrno()
			val status = nativeClose!!.invokeExact(capturedStateSegment, opened) as Int
			if (status == -1) throwLastErrno()
			return true
		}
//		TODO: IPv4 re-consideration
//		println(testSocket(AF_INET, SOCK_STREAM, IPPROTO_TCP))
//		println(testSocket(AF_INET, SOCK_DGRAM, IPPROTO_UDP))
		val tcp6 = testSocket(AF_INET6, SOCK_STREAM, IPPROTO_TCP)
		val udp6 = testSocket(AF_INET6, SOCK_DGRAM, IPPROTO_UDP)
		if (udp6 || tcp6) {
			val socketFeatures = mutableListOf<SystemInternetProtocolV6SocketProviderFeatureImplementation<*>>()
			if (udp6) {
				val datagramProtocols = mutableListOf<SystemInternetProtocolV6DatagramProtocolFeatureImplementation<*>>(
					object : SystemInternetProtocolV6UDPFeature() {
						override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
						override val features: MutableList<IPv6UDPFeatureImplementation<*>> =
							mutableListOf(
								LinuxX64IPv6UDPSocketFeature(),
								LinuxX64IPv6UDPResolutionFeature()
							)
					}
				)
				socketFeatures.add(
					object : SystemInternetProtocolV6DatagramProtocolsSocketProviderFeature() {
						override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
						override val features: MutableList<
								SystemInternetProtocolV6DatagramProtocolFeatureImplementation<*>> = datagramProtocols
					}
				)
			}
			if (tcp6) {
				val streamProtocols = mutableListOf<SystemInternetProtocolV6StreamProtocolFeatureImplementation<*>>(
					object : SystemInternetProtocolV6TCPFeature() {
						override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
						override val features: MutableList<IPv6TCPFeatureImplementation<*>> =
							mutableListOf(
								LinuxX64IPv6TCPSocketFeature(),
								LinuxX64IPv6TCPResolutionFeature()
							)
					}
				)
				socketFeatures.add(
					object : SystemInternetProtocolV6StreamProtocolsSocketProviderFeature() {
						override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
						override val features: MutableList<
								SystemInternetProtocolV6StreamProtocolFeatureImplementation<*>> = streamProtocols
					}
				)
			}
			list.add(
				object : SystemSocketProviderInternetProtocolV6Feature() {
					override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
					override val features: MutableList<SystemInternetProtocolV6SocketProviderFeatureImplementation<*>> =
						socketFeatures
				}
			)
		}
		list
	}

	override fun supported(): Boolean = nativeSocket != null && nativeClose != null
}