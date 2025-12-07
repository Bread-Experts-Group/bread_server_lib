package org.bread_experts_group.api.system.socket.ipv6.stream.tcp.feature.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.AF_INET6
import org.bread_experts_group.api.system.socket.DeferredSocketOperation
import org.bread_experts_group.api.system.socket.feature.SocketConnectFeature
import org.bread_experts_group.api.system.socket.ipv6.InternetProtocolV6AddressPortData
import org.bread_experts_group.api.system.socket.ipv6.connect.IPv6ConnectionDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.connect.IPv6ConnectionFeatureIdentifier
import org.bread_experts_group.api.system.socket.ipv6.connect.IPv6LocalAddressPortData
import org.bread_experts_group.api.system.socket.ipv6.connect.IPv6RemoteAddressPortData
import org.bread_experts_group.api.system.socket.system.DeferredSocketConnect
import org.bread_experts_group.api.system.socket.system.SocketMonitor
import org.bread_experts_group.api.system.socket.system.windows.SOL_SOCKET
import org.bread_experts_group.api.system.socket.system.windows.SO_UPDATE_CONNECT_CONTEXT
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.ffi.windows.threadLocalDWORD0
import org.bread_experts_group.ffi.windows.threadLocalDWORD1
import org.bread_experts_group.ffi.windows.throwLastWSAError
import org.bread_experts_group.ffi.windows.wsa.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class WindowsIPv6TCPConnectFeature(
	private val socket: Long,
	private val monitor: SocketMonitor,
	expresses: FeatureExpression<SocketConnectFeature<IPv6ConnectionFeatureIdentifier, IPv6ConnectionDataIdentifier>>
) : SocketConnectFeature<IPv6ConnectionFeatureIdentifier, IPv6ConnectionDataIdentifier>(expresses) {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun connect(
		vararg features: IPv6ConnectionFeatureIdentifier
	): DeferredSocketOperation<IPv6ConnectionDataIdentifier> =
		object : DeferredSocketConnect<IPv6ConnectionDataIdentifier>(monitor) {
			override fun connect(): List<IPv6ConnectionDataIdentifier> = Arena.ofConfined().use { tempArena ->
				val data = mutableListOf<IPv6ConnectionDataIdentifier>()
				val addresses = features.mapNotNull { it as? InternetProtocolV6AddressPortData }
				if (addresses.isEmpty()) return data
				data.addAll(addresses)
				val list = tempArena.allocate((SOCKET_ADDRESS.byteSize() * addresses.size) + 8)
				list.set(DWORD, 0, addresses.size)
				var nextEntry = list.asSlice(8)
				addresses.forEach {
					val sockAddr = tempArena.allocate(sockaddr_in6)
					sockaddr_in6_sin6_family.set(sockAddr, 0L, AF_INET6.toShort())
					val status = nativeWSAHtons!!.invokeExact(
						capturedStateSegment,
						socket,
						it.port.toShort(),
						threadLocalDWORD0
					) as Int
					if (status != 0) throwLastWSAError()
					sockaddr_in6_sin6_port.set(
						sockAddr, 0L,
						threadLocalDWORD0.get(ValueLayout.JAVA_SHORT, 0)
					)
					MemorySegment.copy(
						it.data, 0,
						(sockaddr_in6_sin6_addr_Byte.invokeExact(sockAddr, 0L) as MemorySegment),
						ValueLayout.JAVA_BYTE, 0, it.data.size
					)
					SOCKET_ADDRESS_lpSockaddr.set(nextEntry, 0L, sockAddr)
					SOCKET_ADDRESS_iSockaddrLength.set(nextEntry, 0L, sockAddr.byteSize().toInt())
					nextEntry = nextEntry.asSlice(SOCKET_ADDRESS.byteSize())
				}
				val localAddr = tempArena.allocate(sockaddr_in6)
				threadLocalDWORD0.set(DWORD, 0, localAddr.byteSize().toInt())
				val remoteAddr = tempArena.allocate(sockaddr_in6)
				threadLocalDWORD1.set(DWORD, 0, remoteAddr.byteSize().toInt())
				var status = nativeWSAConnectByList!!.invokeExact(
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
				status = nativeSetSockOpt!!.invokeExact(
					capturedStateSegment,
					socket,
					SOL_SOCKET,
					SO_UPDATE_CONNECT_CONTEXT,
					MemorySegment.NULL,
					0
				) as Int
				if (status != 0) throwLastWSAError()
				fun MemorySegment.readPort(): UShort {
					val port = sockaddr_in6_sin6_port.get(this, 0L) as Short
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
					IPv6LocalAddressPortData(
						(sockaddr_in6_sin6_addr_Byte.invokeExact(localAddr, 0L) as MemorySegment)
							.toArray(ValueLayout.JAVA_BYTE),
						localAddr.readPort()
					)
				)
				data.add(
					IPv6RemoteAddressPortData(
						(sockaddr_in6_sin6_addr_Byte.invokeExact(remoteAddr, 0L) as MemorySegment)
							.toArray(ValueLayout.JAVA_BYTE),
						remoteAddr.readPort()
					)
				)
				data
			}
		}
}