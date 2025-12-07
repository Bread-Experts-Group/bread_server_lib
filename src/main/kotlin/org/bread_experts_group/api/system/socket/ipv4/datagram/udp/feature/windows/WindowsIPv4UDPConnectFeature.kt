package org.bread_experts_group.api.system.socket.ipv4.datagram.udp.feature.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.AF_INET
import org.bread_experts_group.api.system.socket.DeferredSocketOperation
import org.bread_experts_group.api.system.socket.feature.SocketConnectFeature
import org.bread_experts_group.api.system.socket.ipv4.InternetProtocolV4AddressPortData
import org.bread_experts_group.api.system.socket.ipv4.connect.IPv4ConnectionDataIdentifier
import org.bread_experts_group.api.system.socket.ipv4.connect.IPv4ConnectionFeatureIdentifier
import org.bread_experts_group.api.system.socket.system.DeferredSocketConnect
import org.bread_experts_group.api.system.socket.system.SocketMonitor
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.threadLocalDWORD0
import org.bread_experts_group.ffi.windows.throwLastWSAError
import org.bread_experts_group.ffi.windows.wsa.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class WindowsIPv4UDPConnectFeature(
	private val socket: Long,
	private val monitor: SocketMonitor,
	expresses: FeatureExpression<SocketConnectFeature<IPv4ConnectionFeatureIdentifier, IPv4ConnectionDataIdentifier>>
) : SocketConnectFeature<IPv4ConnectionFeatureIdentifier, IPv4ConnectionDataIdentifier>(expresses) {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun connect(
		vararg features: IPv4ConnectionFeatureIdentifier
	): DeferredSocketOperation<IPv4ConnectionDataIdentifier> =
		object : DeferredSocketConnect<IPv4ConnectionDataIdentifier>(monitor) {
			override fun connect() = Arena.ofConfined().use { tempArena ->
				val data = mutableListOf<IPv4ConnectionDataIdentifier>()
				val address = features.firstNotNullOfOrNull { it as? InternetProtocolV4AddressPortData }
					?: return data
				val addressSockAddr = tempArena.allocate(sockaddr_in)
				sockaddr_in_sin_family.set(addressSockAddr, 0L, AF_INET.toShort())
				var status = nativeWSAHtons!!.invokeExact(
					capturedStateSegment,
					socket,
					address.port.toShort(),
					threadLocalDWORD0
				) as Int
				if (status != 0) throwLastWSAError()
				sockaddr_in_sin_port.set(
					addressSockAddr, 0L,
					threadLocalDWORD0.get(ValueLayout.JAVA_SHORT, 0)
				)
				MemorySegment.copy(
					address.data, 0,
					sockaddr_in_sin_addr.invokeExact(addressSockAddr, 0L) as MemorySegment,
					ValueLayout.JAVA_BYTE, 0,
					address.data.size
				)
				status = nativeWSAConnect!!.invokeExact(
					capturedStateSegment,
					socket,
					addressSockAddr,
					addressSockAddr.byteSize().toInt(),
					MemorySegment.NULL,
					MemorySegment.NULL,
					MemorySegment.NULL,
					MemorySegment.NULL
				) as Int
				if (status != 0) throwLastWSAError()
				data
			}
		}
}