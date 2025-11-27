package org.bread_experts_group.api.system.socket.ipv4.windows

import org.bread_experts_group.api.feature.CheckedImplementation
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.AF_INET
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.IPPROTO_TCP
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.SOCK_STREAM
import org.bread_experts_group.api.system.socket.Socket
import org.bread_experts_group.api.system.socket.SocketFeatures
import org.bread_experts_group.api.system.socket.connect.IPv4TCPConnectionDataIdentifier
import org.bread_experts_group.api.system.socket.connect.IPv4TCPConnectionFeatureIdentifier
import org.bread_experts_group.api.system.socket.connect.IPv4TCPLocalAddressPortData
import org.bread_experts_group.api.system.socket.connect.IPv4TCPRemoteAddressPortData
import org.bread_experts_group.api.system.socket.feature.SocketConnectFeature
import org.bread_experts_group.api.system.socket.feature.SocketFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv4.InternetProtocolV4AddressData
import org.bread_experts_group.api.system.socket.ipv4.InternetProtocolV4AddressPortData
import org.bread_experts_group.api.system.socket.ipv4.stream.tcp.feature.IPV4TCPSocketFeature
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.ffi.windows.threadLocalDWORD0
import org.bread_experts_group.ffi.windows.threadLocalDWORD1
import org.bread_experts_group.ffi.windows.throwLastWSAError
import org.bread_experts_group.ffi.windows.wsa.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class WindowsIPV4TCPSocketFeature : IPV4TCPSocketFeature(), CheckedImplementation {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeSocket != null
	override fun openSocket(): Socket<InternetProtocolV4AddressData> {
		val socket = nativeSocket!!.invokeExact(
			capturedStateSegment,
			AF_INET,
			SOCK_STREAM,
			IPPROTO_TCP
		) as Long
		if (socket == INVALID_SOCKET) throwLastWSAError()
		return object : Socket<InternetProtocolV4AddressData>() {
			override val features: MutableList<SocketFeatureImplementation<*>> = mutableListOf(
				object : SocketConnectFeature<IPv4TCPConnectionFeatureIdentifier, IPv4TCPConnectionDataIdentifier>(
					SocketFeatures.CONNECT_IPV4
				) {
					override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
					override fun connect(
						vararg features: IPv4TCPConnectionFeatureIdentifier
					): List<IPv4TCPConnectionDataIdentifier> = Arena.ofConfined().use { tempArena ->
						val data = mutableListOf<IPv4TCPConnectionDataIdentifier>()
						val addresses = features.mapNotNull { it as? InternetProtocolV4AddressPortData }
						if (addresses.isEmpty()) return data
						data.addAll(addresses)
						val list = tempArena.allocate((SOCKET_ADDRESS.byteSize() * addresses.size) + 8)
						list.set(DWORD, 0, addresses.size)
						var nextEntry = list.asSlice(8)
						addresses.forEach {
							val sockAddr = tempArena.allocate(sockaddr_in)
							sockaddr_in_sin_family.set(sockAddr, 0L, AF_INET.toShort())
							val status = nativeWSAHtons!!.invokeExact(
								capturedStateSegment,
								socket,
								it.port.toShort(),
								threadLocalDWORD0
							) as Int
							if (status != 0) throwLastWSAError()
							sockaddr_in_sin_port.set(
								sockAddr, 0L,
								threadLocalDWORD0.get(ValueLayout.JAVA_SHORT, 0)
							)
							MemorySegment.copy(
								it.data, 0,
								(sockaddr_in_sin_addr.invokeExact(sockAddr, 0L) as MemorySegment),
								ValueLayout.JAVA_BYTE, 0, it.data.size
							)
							SOCKET_ADDRESS_lpSockaddr.set(nextEntry, 0L, sockAddr)
							SOCKET_ADDRESS_iSockaddrLength.set(nextEntry, 0L, sockAddr.byteSize().toInt())
							nextEntry = nextEntry.asSlice(SOCKET_ADDRESS.byteSize())
						}
						val localAddr = tempArena.allocate(sockaddr_in)
						threadLocalDWORD0.set(DWORD, 0, localAddr.byteSize().toInt())
						val remoteAddr = tempArena.allocate(sockaddr_in)
						threadLocalDWORD1.set(DWORD, 0, remoteAddr.byteSize().toInt())
						val status = nativeWSAConnectByList!!.invokeExact(
							capturedStateSegment,
							socket,
							list,
							threadLocalDWORD0,
							localAddr,
							threadLocalDWORD1,
							remoteAddr,
							MemorySegment.NULL,
							MemorySegment.NULL
						) as Int
						if (status == 0) throwLastWSAError()

						fun MemorySegment.readPort(): UShort {
							val port = sockaddr_in_sin_port.get(this, 0L) as Short
							val status = nativeWSANtohs!!.invokeExact(
								capturedStateSegment,
								socket,
								port,
								threadLocalDWORD0
							) as Int
							if (status != 0) throwLastWSAError()
							return threadLocalDWORD0.get(ValueLayout.JAVA_SHORT, 0).toUShort()
						}

						data.add(
							IPv4TCPLocalAddressPortData(
								(sockaddr_in_sin_addr.invokeExact(localAddr, 0L) as MemorySegment)
									.toArray(ValueLayout.JAVA_BYTE),
								localAddr.readPort()
							)
						)
						data.add(
							IPv4TCPRemoteAddressPortData(
								(sockaddr_in_sin_addr.invokeExact(remoteAddr, 0L) as MemorySegment)
									.toArray(ValueLayout.JAVA_BYTE),
								remoteAddr.readPort()
							)
						)
						data
					}
				}
			)
		}
	}
}