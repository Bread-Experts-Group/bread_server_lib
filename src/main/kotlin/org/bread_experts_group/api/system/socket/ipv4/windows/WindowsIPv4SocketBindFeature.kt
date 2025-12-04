package org.bread_experts_group.api.system.socket.ipv4.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.AF_INET
import org.bread_experts_group.api.system.socket.feature.SocketBindFeature
import org.bread_experts_group.api.system.socket.ipv4.InternetProtocolV4AddressData
import org.bread_experts_group.api.system.socket.ipv4.InternetProtocolV4AddressPortData
import org.bread_experts_group.api.system.socket.ipv4.bind.IPv4BindDataIdentifier
import org.bread_experts_group.api.system.socket.ipv4.bind.IPv4BindFeatureIdentifier
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.threadLocalDWORD0
import org.bread_experts_group.ffi.windows.throwLastWSAError
import org.bread_experts_group.ffi.windows.wsa.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class WindowsIPv4SocketBindFeature(
	private val socket: Long,
	expresses: FeatureExpression<SocketBindFeature<IPv4BindFeatureIdentifier, IPv4BindDataIdentifier>>
) : SocketBindFeature<IPv4BindFeatureIdentifier, IPv4BindDataIdentifier>(expresses) {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun bind(vararg features: IPv4BindFeatureIdentifier): List<IPv4BindDataIdentifier> {
		val address = features.firstNotNullOfOrNull { it as? InternetProtocolV4AddressData }
			?: return emptyList()
		Arena.ofConfined().use { tempArena ->
			val sockAddr = tempArena.allocate(sockaddr_in)
			sockaddr_in_sin_family.set(sockAddr, 0L, AF_INET.toShort())
			if (address is InternetProtocolV4AddressPortData) {
				val status = nativeWSAHtons!!.invokeExact(
					capturedStateSegment,
					socket,
					address.port.toShort(),
					threadLocalDWORD0
				) as Int
				if (status != 0) throwLastWSAError()
				sockaddr_in_sin_port.set(
					sockAddr, 0L,
					threadLocalDWORD0.get(ValueLayout.JAVA_SHORT, 0)
				)
			}
			MemorySegment.copy(
				address.data, 0,
				(sockaddr_in_sin_addr.invokeExact(sockAddr, 0L) as MemorySegment),
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