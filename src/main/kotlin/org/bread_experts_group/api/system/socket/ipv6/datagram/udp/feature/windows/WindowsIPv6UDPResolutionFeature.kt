package org.bread_experts_group.api.system.socket.ipv6.datagram.udp.feature.windows

import org.bread_experts_group.api.feature.CheckedImplementation
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.AF_INET6
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.IPPROTO_UDP
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.SOCK_DGRAM
import org.bread_experts_group.api.system.socket.ipv6.datagram.udp.feature.IPv6UDPResolutionFeature
import org.bread_experts_group.api.system.socket.resolution.ResolutionDataIdentifier
import org.bread_experts_group.api.system.socket.resolution.ResolutionFeatureIdentifier
import org.bread_experts_group.api.system.socket.sys_feature.windows.winResolve
import org.bread_experts_group.ffi.windows.wsa.nativeFreeAddrInfoExWide
import org.bread_experts_group.ffi.windows.wsa.nativeGetAddrInfoExWide

class WindowsIPv6UDPResolutionFeature : IPv6UDPResolutionFeature(), CheckedImplementation {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeGetAddrInfoExWide != null && nativeFreeAddrInfoExWide != null
	override fun resolve(
		hostName: String,
		serviceName: String,
		vararg features: ResolutionFeatureIdentifier
	): List<ResolutionDataIdentifier> = winResolve(
		hostName,
		serviceName,
		AF_INET6,
		SOCK_DGRAM,
		IPPROTO_UDP,
		*features
	)
}