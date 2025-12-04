@file:Suppress("LongLine")

package org.bread_experts_group.api.system.feature.windows

import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.feature.SystemNetworkingSocketsFeature
import org.bread_experts_group.api.system.socket.SystemSocketProviderFeatures
import org.bread_experts_group.api.system.socket.ipv4.datagram.feature.SystemInternetProtocolV4DatagramProtocolFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv4.datagram.feature.SystemInternetProtocolV4UDPFeature
import org.bread_experts_group.api.system.socket.ipv4.datagram.udp.feature.IPv4UDPFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv4.datagram.udp.feature.IPv4UDPSystemLabelFeature
import org.bread_experts_group.api.system.socket.ipv4.datagram.udp.feature.windows.WindowsIPv4UDPResolutionFeature
import org.bread_experts_group.api.system.socket.ipv4.datagram.udp.feature.windows.WindowsIPv4UDPSocketFeature
import org.bread_experts_group.api.system.socket.ipv4.feature.SystemInternetProtocolV4DatagramProtocolsSocketProviderFeature
import org.bread_experts_group.api.system.socket.ipv4.feature.SystemInternetProtocolV4SocketProviderFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv4.feature.SystemInternetProtocolV4StreamProtocolsSocketProviderFeature
import org.bread_experts_group.api.system.socket.ipv4.stream.feature.SystemInternetProtocolV4StreamProtocolFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv4.stream.feature.SystemInternetProtocolV4TCPFeature
import org.bread_experts_group.api.system.socket.ipv4.stream.tcp.feature.IPv4TCPFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv4.stream.tcp.feature.IPv4TCPSystemLabelFeature
import org.bread_experts_group.api.system.socket.ipv4.stream.tcp.feature.windows.WindowsIPv4TCPResolutionFeature
import org.bread_experts_group.api.system.socket.ipv4.stream.tcp.feature.windows.WindowsIPv4TCPSocketFeature
import org.bread_experts_group.api.system.socket.ipv4.windows.*
import org.bread_experts_group.api.system.socket.ipv6.datagram.feature.SystemInternetProtocolV6DatagramProtocolFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv6.datagram.feature.SystemInternetProtocolV6UDPFeature
import org.bread_experts_group.api.system.socket.ipv6.datagram.udp.feature.IPv6UDPFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv6.datagram.udp.feature.IPv6UDPSystemLabelFeature
import org.bread_experts_group.api.system.socket.ipv6.datagram.udp.feature.windows.WindowsIPv6UDPResolutionFeature
import org.bread_experts_group.api.system.socket.ipv6.datagram.udp.feature.windows.WindowsIPv6UDPSocketFeature
import org.bread_experts_group.api.system.socket.ipv6.feature.SystemInternetProtocolV6DatagramProtocolsSocketProviderFeature
import org.bread_experts_group.api.system.socket.ipv6.feature.SystemInternetProtocolV6SocketProviderFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv6.feature.SystemInternetProtocolV6StreamProtocolsSocketProviderFeature
import org.bread_experts_group.api.system.socket.ipv6.stream.feature.SystemInternetProtocolV6StreamProtocolFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv6.stream.feature.SystemInternetProtocolV6TCPFeature
import org.bread_experts_group.api.system.socket.ipv6.stream.tcp.feature.IPv6TCPFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv6.stream.tcp.feature.IPv6TCPSystemLabelFeature
import org.bread_experts_group.api.system.socket.ipv6.stream.tcp.feature.windows.WindowsIPv6TCPResolutionFeature
import org.bread_experts_group.api.system.socket.ipv6.stream.tcp.feature.windows.WindowsIPv6TCPSocketFeature
import org.bread_experts_group.api.system.socket.resolution_namespace_provider.ResolutionNamespaceProvider
import org.bread_experts_group.api.system.socket.resolution_namespace_provider.ResolutionNamespaceProviderFeatures
import org.bread_experts_group.api.system.socket.resolution_namespace_provider.feature.ResolutionNamespaceProviderFeatureImplementation
import org.bread_experts_group.api.system.socket.resolution_namespace_provider.feature.ResolutionNamespaceProviderSystemIdentifierFeature
import org.bread_experts_group.api.system.socket.resolution_namespace_provider.type.ResolutionNamespaceSystemType
import org.bread_experts_group.api.system.socket.resolution_namespace_provider.type.ResolutionNamespaceTypeIdentifier
import org.bread_experts_group.api.system.socket.resolution_namespace_provider.type.StandardResolutionNamespaceTypes
import org.bread_experts_group.api.system.socket.resolution_namespace_provider.type.WindowsResolutionNamespaceTypes
import org.bread_experts_group.api.system.socket.sys_feature.*
import org.bread_experts_group.ffi.GUID
import org.bread_experts_group.ffi.windows.*
import org.bread_experts_group.ffi.windows.wsa.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsSystemNetworkingSocketsFeature : SystemNetworkingSocketsFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean {
		if (nativeWSCEnumProtocols == null) return false
		Arena.ofConfined().use { tempArena ->
			val wsaData = tempArena.allocate(WSAData)
			val status = (nativeWSAStartup ?: return false).invokeExact(
				0x0202.toShort(),
				wsaData
			) as Int
			if (status != 0) decodeWin32Error(status)
			features.add(
				SystemSocketProviderTextualFeature(
					ImplementationSource.SYSTEM_NATIVE,
					SystemSocketProviderFeatures.PROVIDER_DESCRIPTION,
					(WSAData_szDescription.invokeExact(wsaData, 0L) as MemorySegment)
						.getString(0, Charsets.US_ASCII)
				)
			)
			features.add(
				SystemSocketProviderTextualFeature(
					ImplementationSource.SYSTEM_NATIVE,
					SystemSocketProviderFeatures.PROVIDER_SYSTEM_STATUS,
					(WSAData_szSystemStatus.invokeExact(wsaData, 0L) as MemorySegment)
						.getString(0, Charsets.US_ASCII)
				)
			)
		}
		return true
	}

	companion object {
		const val AF_UNIX = 1
		const val AF_INET = 2
		const val AF_INET6 = 23
		const val AF_NETDES = 28
		const val AF_BTH = 32
		const val AF_HYPERV = 34

		const val SOCK_STREAM = 1
		const val SOCK_DGRAM = 2
		const val SOCK_RAW = 3

		const val IPPROTO_IP = 0
		const val IPPROTO_TCP = 6
		const val IPPROTO_UDP = 17

		const val BTHPROTO_RFCOMM = 0x0003
		const val BTHPROTO_L2CAP = 0x0100

		const val HV_PROTOCOL_RAW = 1
	}

	override val features: MutableList<SystemSocketProviderFeatureImplementation<*>> by lazy {
		val implementations = mutableListOf<SystemSocketProviderFeatureImplementation<*>>()
		if (nativeWSAEnumNameSpaceProvidersW != null) implementations.add(
			object : SystemSocketProvideResolutionNamespaceProvidersFeature() {
				override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
				override fun iterator(): Iterator<ResolutionNamespaceProvider> {
					threadLocalDWORD0.set(DWORD, 0, 0)
					nativeWSAEnumNameSpaceProvidersW.invokeExact(
						threadLocalDWORD0,
						MemorySegment.NULL
					) as Int
					val arena = Arena.ofConfined()
					val providerData = arena.allocate(threadLocalDWORD0.get(DWORD, 0).toLong())
					val providers = nativeWSAEnumNameSpaceProvidersW.invokeExact(
						threadLocalDWORD0,
						providerData
					) as Int
					if (providers == SOCKET_ERROR) throwLastWSAError()
					var providerInfo = providerData
					val iterable = List(providers) {
						object : ResolutionNamespaceProvider() {
							override val features: MutableList<ResolutionNamespaceProviderFeatureImplementation<*>> =
								mutableListOf(
									ResolutionNamespaceProviderSystemIdentifierFeature(
										ImplementationSource.SYSTEM_NATIVE,
										ResolutionNamespaceProviderFeatures.SYSTEM_IDENTIFIER,
										GUID(
											WSANAMESPACE_INFOW_NSProviderId.invokeExact(
												providerInfo, 0L
											) as MemorySegment
										)
									),
									ResolutionNamespaceProviderSystemIdentifierFeature(
										ImplementationSource.SYSTEM_NATIVE,
										ResolutionNamespaceProviderFeatures.SYSTEM_LABEL,
										(WSANAMESPACE_INFOW_lpszIdentifier.get(
											providerInfo, 0L
										) as MemorySegment)
											.reinterpret(Long.MAX_VALUE)
											.getString(0, Charsets.UTF_16LE)
									),
									ResolutionNamespaceProviderSystemIdentifierFeature(
										ImplementationSource.SYSTEM_NATIVE,
										ResolutionNamespaceProviderFeatures.SYSTEM_VERSION,
										WSANAMESPACE_INFOW_dwVersion.get(providerInfo, 0L) as Int
									)
								)

							override val type: ResolutionNamespaceTypeIdentifier = when (
								val typeRaw = WSANAMESPACE_INFOW_dwNameSpace.get(providerInfo, 0L) as Int
							) {
								NS_DNS -> StandardResolutionNamespaceTypes.DOMAIN_NAME_SYSTEM
								NS_NLA -> WindowsResolutionNamespaceTypes.NETWORK_LOCATION_AWARENESS
								NS_BTH -> StandardResolutionNamespaceTypes.BLUETOOTH
								NS_NTDS -> WindowsResolutionNamespaceTypes.NT_DIRECTORY_SERVICE
								NS_EMAIL -> StandardResolutionNamespaceTypes.EMAIL
								NS_PNRPNAME -> WindowsResolutionNamespaceTypes.P2P_NAME
								NS_PNRPCLOUD -> WindowsResolutionNamespaceTypes.P2P_COLLECTION
								else -> ResolutionNamespaceSystemType(typeRaw)
							}

							override val enabled: Boolean = WSANAMESPACE_INFOW_fActive.get(providerInfo, 0) != 0
						}.also {
							providerInfo = providerInfo.asSlice(WSANAMESPACE_INFOW.byteSize())
						}
					}
					arena.close()
					return iterable.iterator()
				}
			}
		)
		threadLocalDWORD0.set(DWORD, 0, 0)
		nativeWSCEnumProtocols!!.invokeExact(
			MemorySegment.NULL,
			MemorySegment.NULL,
			threadLocalDWORD0,
			threadLocalDWORD1
		) as Int
		Arena.ofConfined().use { tempArena ->
			val protocolData = tempArena.allocate(threadLocalDWORD0.get(DWORD, 0).toLong())
			val protocols = nativeWSCEnumProtocols.invokeExact(
				MemorySegment.NULL,
				protocolData,
				threadLocalDWORD0,
				threadLocalDWORD1
			) as Int
			if (protocols == SOCKET_ERROR) decodeWin32Error(threadLocalDWORD1.get(DWORD, 0))
			var protocolInfo = protocolData
			val ipv4 = mutableMapOf<Int, SystemInternetProtocolV4SocketProviderFeatureImplementation<*>>()
			val ipv6 = mutableMapOf<Int, SystemInternetProtocolV6SocketProviderFeatureImplementation<*>>()
			repeat(protocols) {
				val socketType = WSAPROTOCOL_INFOW_iSocketType.get(protocolInfo, 0L) as Int
				val protocol = WSAPROTOCOL_INFOW_iProtocol.get(protocolInfo, 0L) as Int
				val label = (WSAPROTOCOL_INFOW_szProtocol.invokeExact(protocolInfo, 0L) as MemorySegment)
					.getString(0, Charsets.UTF_16LE)
				when (val addressFamily = WSAPROTOCOL_INFOW_iAddressFamily.get(protocolInfo, 0L) as Int) {
					AF_UNIX -> when (socketType) {
						SOCK_STREAM -> when (protocol) {
							0 -> logger.severe("STD/UNIX")
							else -> logger.warning("Unknown WSA/Unix/Stream iProtocol [$protocol]")
						}

						else -> logger.warning("Unknown WSA/Unix iSocketType [$socketType]")
					}

					AF_INET -> when (socketType) {
						SOCK_STREAM -> {
							@Suppress("UNCHECKED_CAST")
							val streamProtocols = ipv4.getOrPut(SOCK_STREAM) {
								object : SystemInternetProtocolV4StreamProtocolsSocketProviderFeature() {
									override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
									override val features: MutableList<
											SystemInternetProtocolV4StreamProtocolFeatureImplementation<*>> =
										mutableListOf()
								}
							} as FeatureProvider<SystemInternetProtocolV4StreamProtocolFeatureImplementation<*>>
							when (protocol) {
								IPPROTO_TCP -> streamProtocols.features.add(
									object : SystemInternetProtocolV4TCPFeature() {
										override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
										override val features: MutableList<IPv4TCPFeatureImplementation<*>> =
											mutableListOf(
												IPv4TCPSystemLabelFeature(
													ImplementationSource.SYSTEM_NATIVE,
													label
												),
												WindowsIPv4TCPResolutionFeature(),
												WindowsIPv4TCPSocketFeature()
											)
									}
								)

								else -> logger.warning("Unknown WSA/IPv4/Stream iProtocol [$protocol]")
							}
						}

						SOCK_DGRAM -> {
							@Suppress("UNCHECKED_CAST")
							val datagramProtocols = ipv4.getOrPut(SOCK_DGRAM) {
								object : SystemInternetProtocolV4DatagramProtocolsSocketProviderFeature() {
									override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
									override val features: MutableList<
											SystemInternetProtocolV4DatagramProtocolFeatureImplementation<*>> =
										mutableListOf()
								}
							} as FeatureProvider<SystemInternetProtocolV4DatagramProtocolFeatureImplementation<*>>
							when (protocol) {
								IPPROTO_UDP -> datagramProtocols.features.add(
									object : SystemInternetProtocolV4UDPFeature() {
										override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
										override val features: MutableList<IPv4UDPFeatureImplementation<*>> =
											mutableListOf(
												IPv4UDPSystemLabelFeature(
													ImplementationSource.SYSTEM_NATIVE,
													label
												),
												WindowsIPv4UDPResolutionFeature(),
												WindowsIPv4UDPSocketFeature()
											)
									}
								)

								else -> logger.warning("Unknown WSA/IPv4/Datagram iProtocol [$protocol]")
							}
						}

						SOCK_RAW -> when (protocol) {
							IPPROTO_IP -> logger.severe("Raw/IPv4")
							else -> logger.warning("Unknown WSA/IPv4/Raw iProtocol [$protocol]")
						}

						else -> logger.warning("Unknown WSA/IPv4 iSocketType [$socketType]")
					}

					AF_INET6 -> when (socketType) {
						SOCK_STREAM -> {
							@Suppress("UNCHECKED_CAST")
							val streamProtocols = ipv6.getOrPut(SOCK_STREAM) {
								object : SystemInternetProtocolV6StreamProtocolsSocketProviderFeature() {
									override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
									override val features: MutableList<
											SystemInternetProtocolV6StreamProtocolFeatureImplementation<*>> =
										mutableListOf()
								}
							} as FeatureProvider<SystemInternetProtocolV6StreamProtocolFeatureImplementation<*>>
							when (protocol) {
								IPPROTO_TCP -> streamProtocols.features.add(
									object : SystemInternetProtocolV6TCPFeature() {
										override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
										override val features: MutableList<IPv6TCPFeatureImplementation<*>> =
											mutableListOf(
												IPv6TCPSystemLabelFeature(
													ImplementationSource.SYSTEM_NATIVE,
													label
												),
												WindowsIPv6TCPResolutionFeature(),
												WindowsIPv6TCPSocketFeature()
											)
									}
								)

								else -> logger.warning("Unknown WSA/IPv6/Stream iProtocol [$protocol]")
							}
						}

						SOCK_DGRAM -> {
							@Suppress("UNCHECKED_CAST")
							val datagramProtocols = ipv6.getOrPut(SOCK_DGRAM) {
								object : SystemInternetProtocolV6DatagramProtocolsSocketProviderFeature() {
									override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
									override val features: MutableList<
											SystemInternetProtocolV6DatagramProtocolFeatureImplementation<*>> =
										mutableListOf()
								}
							} as FeatureProvider<SystemInternetProtocolV6DatagramProtocolFeatureImplementation<*>>
							when (protocol) {
								IPPROTO_UDP -> datagramProtocols.features.add(
									object : SystemInternetProtocolV6UDPFeature() {
										override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
										override val features: MutableList<IPv6UDPFeatureImplementation<*>> =
											mutableListOf(
												IPv6UDPSystemLabelFeature(
													ImplementationSource.SYSTEM_NATIVE,
													label
												),
												WindowsIPv6UDPResolutionFeature(),
												WindowsIPv6UDPSocketFeature()
											)
									}
								)

								else -> logger.warning("Unknown WSA/IPv6/Datagram iProtocol [$protocol]")
							}
						}

						SOCK_RAW -> when (protocol) {
							IPPROTO_IP -> logger.severe("Raw/IPv6")
							else -> logger.warning("Unknown WSA/IPv6/Raw iProtocol [$protocol]")
						}

						else -> logger.warning("Unknown WSA/IPv6 iSocketType [$socketType]")
					}

					AF_NETDES -> when (socketType) {
						SOCK_STREAM -> when (protocol) {
							0 -> {} // ???
							else -> logger.warning("Unknown WSA/NetworkDesigners/Stream iProtocol [$protocol]")
						}

						SOCK_DGRAM -> when (protocol) {
							0 -> {} // ???
							else -> logger.warning("Unknown WSA/NetworkDesigners/Datagram iProtocol [$protocol]")
						}

						else -> logger.warning("Unknown WSA/NetworkDesigners iSocketType [$socketType]")
					}

					AF_BTH -> when (socketType) {
						SOCK_STREAM -> when (protocol) {
							BTHPROTO_RFCOMM -> logger.severe("BTH/RFComm")
							BTHPROTO_L2CAP -> logger.severe("BTH/L2Cap")

							else -> logger.warning("Unknown WSA/Bluetooth/Stream iProtocol [$protocol]")
						}

						else -> logger.warning("Unknown WSA/Bluetooth iSocketType [$socketType]")
					}

					AF_HYPERV -> when (socketType) {
						SOCK_STREAM -> when (protocol) {
							HV_PROTOCOL_RAW -> logger.severe("HyperV/L2Cap")
							else -> logger.warning("Unknown WSA/HyperV/Stream iProtocol [$protocol]")
						}

						else -> logger.warning("Unknown WSA/HyperV iSocketType [$socketType]")
					}

					else -> logger.warning("Unknown WSA iAddressFamily [$addressFamily]")
				}
				protocolInfo = protocolInfo.asSlice(WSAPROTOCOL_INFOW.byteSize())
			}
			if (ipv4.isNotEmpty()) implementations.add(object : SystemSocketProviderInternetProtocolV4Feature() {
				override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
				override val features: MutableList<SystemInternetProtocolV4SocketProviderFeatureImplementation<*>> =
					ipv4.values.toMutableList()
			})
			if (ipv6.isNotEmpty()) implementations.add(object : SystemSocketProviderInternetProtocolV6Feature() {
				override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
				override val features: MutableList<SystemInternetProtocolV6SocketProviderFeatureImplementation<*>> =
					ipv6.values.toMutableList()
			})
		}
		implementations
	}
}