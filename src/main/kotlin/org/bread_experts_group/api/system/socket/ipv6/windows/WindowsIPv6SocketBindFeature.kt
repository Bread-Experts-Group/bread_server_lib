package org.bread_experts_group.api.system.socket.ipv6.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.AF_INET6
import org.bread_experts_group.api.system.socket.feature.SocketBindFeature
import org.bread_experts_group.api.system.socket.ipv6.InternetProtocolV6AddressData
import org.bread_experts_group.api.system.socket.ipv6.InternetProtocolV6AddressPortData
import org.bread_experts_group.api.system.socket.ipv6.bind.IPv6BindDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.bind.IPv6BindFeatureIdentifier
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.threadLocalDWORD0
import org.bread_experts_group.ffi.windows.throwLastWSAError
import org.bread_experts_group.ffi.windows.wsa.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class WindowsIPv6SocketBindFeature(
	private val socket: Long,
	expresses: FeatureExpression<SocketBindFeature<IPv6BindFeatureIdentifier, IPv6BindDataIdentifier>>
) : SocketBindFeature<IPv6BindFeatureIdentifier, IPv6BindDataIdentifier>(expresses) {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun bind(vararg features: IPv6BindFeatureIdentifier): List<IPv6BindDataIdentifier> {
		val address = features.firstNotNullOfOrNull { it as? InternetProtocolV6AddressData }
			?: return emptyList()
		Arena.ofConfined().use { tempArena ->
			val sockAddr = tempArena.allocate(sockaddr_in6)
			sockaddr_in6_sin6_family.set(sockAddr, 0L, AF_INET6.toShort())
			if (address is InternetProtocolV6AddressPortData) {
				val status = nativeWSAHtons!!.invokeExact(
					capturedStateSegment,
					socket,
					address.port.toShort(),
					threadLocalDWORD0
				) as Int
				if (status != 0) throwLastWSAError()
				sockaddr_in6_sin6_port.set(
					sockAddr, 0L,
					threadLocalDWORD0.get(ValueLayout.JAVA_SHORT, 0)
				)
			}
			MemorySegment.copy(
				address.data, 0,
				(sockaddr_in6_sin6_addr_Byte.invokeExact(sockAddr, 0L) as MemorySegment),
				ValueLayout.JAVA_BYTE, 0, address.data.size
			)
			val status = nativeBind!!.invokeExact(
				capturedStateSegment,
				socket,
				sockAddr,
				sockAddr.byteSize().toInt()
			) as Int
			if (status != 0) throwLastWSAError()
		}
		return listOf(address)
	}
}