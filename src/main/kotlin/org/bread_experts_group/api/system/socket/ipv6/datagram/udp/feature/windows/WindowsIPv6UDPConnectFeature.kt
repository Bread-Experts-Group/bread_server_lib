package org.bread_experts_group.api.system.socket.ipv6.datagram.udp.feature.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.AF_INET6
import org.bread_experts_group.api.system.socket.DeferredOperation
import org.bread_experts_group.api.system.socket.feature.SocketConnectFeature
import org.bread_experts_group.api.system.socket.ipv6.InternetProtocolV6AddressPortData
import org.bread_experts_group.api.system.socket.ipv6.connect.IPv6ConnectionDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.connect.IPv6ConnectionFeatureIdentifier
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.threadLocalDWORD0
import org.bread_experts_group.ffi.windows.throwLastWSAError
import org.bread_experts_group.ffi.windows.wsa.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.util.concurrent.TimeUnit

class WindowsIPv6UDPConnectFeature(
	private val socket: Long,
	expresses: FeatureExpression<SocketConnectFeature<IPv6ConnectionFeatureIdentifier, IPv6ConnectionDataIdentifier>>
) : SocketConnectFeature<IPv6ConnectionFeatureIdentifier, IPv6ConnectionDataIdentifier>(expresses) {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun connect(
		vararg features: IPv6ConnectionFeatureIdentifier
	): DeferredOperation<IPv6ConnectionDataIdentifier> {
		val data = mutableListOf<IPv6ConnectionDataIdentifier>()
		Arena.ofConfined().use { tempArena ->
			val address = features.firstNotNullOfOrNull { it as? InternetProtocolV6AddressPortData }
				?: return DeferredOperation.Immediate(emptyList())
			val addressSockAddr = tempArena.allocate(sockaddr_in6)
			sockaddr_in6_sin6_family.set(addressSockAddr, 0L, AF_INET6.toShort())
			var status = nativeWSAHtons!!.invokeExact(
				capturedStateSegment,
				socket,
				address.port.toShort(),
				threadLocalDWORD0
			) as Int
			if (status != 0) throwLastWSAError()
			sockaddr_in6_sin6_port.set(
				addressSockAddr, 0L,
				threadLocalDWORD0.get(ValueLayout.JAVA_SHORT, 0)
			)
			MemorySegment.copy(
				address.data, 0,
				sockaddr_in6_sin6_addr_Byte.invokeExact(addressSockAddr, 0L) as MemorySegment,
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
		}

		return object : DeferredOperation<IPv6ConnectionDataIdentifier> {
			override fun block(): List<IPv6ConnectionDataIdentifier> = data
			override fun block(time: Long, unit: TimeUnit): List<IPv6ConnectionDataIdentifier> = data
		}
	}
}