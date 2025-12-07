package org.bread_experts_group.api.system.feature.linux

import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.feature.SystemNetworkingSocketsFeature
import org.bread_experts_group.api.system.socket.ipv4.feature.SystemInternetProtocolV4SocketProviderFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv6.feature.SystemInternetProtocolV6SocketProviderFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv6.feature.SystemInternetProtocolV6StreamProtocolsSocketProviderFeature
import org.bread_experts_group.api.system.socket.ipv6.stream.feature.SystemInternetProtocolV6StreamProtocolFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv6.stream.feature.SystemInternetProtocolV6TCPFeature
import org.bread_experts_group.api.system.socket.ipv6.stream.tcp.feature.IPv6TCPFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv6.stream.tcp.feature.posix.POSIXIPv6TCPResolutionFeature
import org.bread_experts_group.api.system.socket.sys_feature.SystemSocketProviderFeatureImplementation
import org.bread_experts_group.api.system.socket.sys_feature.SystemSocketProviderInternetProtocolV4Feature
import org.bread_experts_group.api.system.socket.sys_feature.SystemSocketProviderInternetProtocolV6Feature
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.posix.*

class LinuxSystemNetworkingSocketsFeature : SystemNetworkingSocketsFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeSocket != null

	companion object {
		const val AF_INET = 2
		const val AF_INET6 = 10

		const val SOCK_STREAM = 1
		const val SOCK_DGRAM = 2

		const val IPPROTO_TCP = 6
		const val IPPROTO_UDP = 17
		const val IPPROTO_IPV6 = 41

		const val IPV6_V6ONLY = 26
	}

	override val features: MutableList<SystemSocketProviderFeatureImplementation<*>> by lazy {
		val implementations = mutableListOf<SystemSocketProviderFeatureImplementation<*>>()
		fun testSocket(domain: Int, type: Int, protocol: Int): Boolean {
			val socket = nativeSocket!!.invokeExact(capturedStateSegment, domain, type, protocol) as Int
			try {
				if (socket == -1) throwLastErrno()
			} catch (e: POSIXErrnoException) {
				if (e.error.enum == POSIXErrno.EAFNOSUPPORT) return false
				throw e
			} finally {
				val status = nativeClose!!.invokeExact(capturedStateSegment, socket) as Int
				if (status != 0) throwLastErrno()
			}
			return true
		}

		val ipv4DatagramUDP = testSocket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)
		val ipv4StreamTCP = testSocket(AF_INET, SOCK_STREAM, IPPROTO_TCP)
		if (ipv4DatagramUDP || ipv4StreamTCP) {
			val ipv4 = mutableMapOf<Int, SystemInternetProtocolV4SocketProviderFeatureImplementation<*>>()
//			if (ipv4DatagramUDP) TODO: IPv4 Datagram UDP
//			if (ipv4StreamTCP) TODO: IPv4 Stream TCP
			implementations.add(object : SystemSocketProviderInternetProtocolV4Feature() {
				override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
				override val features: MutableList<SystemInternetProtocolV4SocketProviderFeatureImplementation<*>> =
					ipv4.values.toMutableList()
			})
		}

		val ipv6DatagramUDP = testSocket(AF_INET6, SOCK_DGRAM, IPPROTO_UDP)
		val ipv6StreamTCP = testSocket(AF_INET6, SOCK_STREAM, IPPROTO_TCP)
		if (ipv6DatagramUDP || ipv6StreamTCP) {
			val ipv6 = mutableMapOf<Int, SystemInternetProtocolV6SocketProviderFeatureImplementation<*>>()
//			if (ipv6DatagramUDP) TODO: IPv6 Datagram UDP
			if (ipv6StreamTCP) {
				@Suppress("UNCHECKED_CAST")
				val streamProtocols = ipv6.getOrPut(SOCK_STREAM) {
					object : SystemInternetProtocolV6StreamProtocolsSocketProviderFeature() {
						override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
						override val features: MutableList<
								SystemInternetProtocolV6StreamProtocolFeatureImplementation<*>> =
							mutableListOf()
					}
				} as FeatureProvider<SystemInternetProtocolV6StreamProtocolFeatureImplementation<*>>

				// Make sure to conditionalize based on future stream protocols
				streamProtocols.features.add(
					object : SystemInternetProtocolV6TCPFeature() {
						override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
						override val features: MutableList<IPv6TCPFeatureImplementation<*>> =
							mutableListOf(
								POSIXIPv6TCPResolutionFeature(),
								LinuxIPv6TCPSocketFeature()
							)
					}
				)
			}

			implementations.add(object : SystemSocketProviderInternetProtocolV6Feature() {
				override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
				override val features: MutableList<SystemInternetProtocolV6SocketProviderFeatureImplementation<*>> =
					ipv6.values.toMutableList()
			})
		}
		implementations
	}
}