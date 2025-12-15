package org.bread_experts_group.api.system.socket.ipv4.datagram.udp.feature.windows

import org.bread_experts_group.api.feature.CheckedImplementation
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.AF_INET
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.IPPROTO_UDP
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.SOCK_DGRAM
import org.bread_experts_group.api.system.socket.ipv4.datagram.udp.feature.IPv4UDPResolutionFeature
import org.bread_experts_group.api.system.socket.resolution.ResolutionDataIdentifier
import org.bread_experts_group.api.system.socket.resolution.ResolutionFeatureIdentifier
import org.bread_experts_group.api.system.socket.system.windows.winResolve
import org.bread_experts_group.ffi.windows.wsa.nativeFreeAddrInfoExWide
import org.bread_experts_group.ffi.windows.wsa.nativeGetAddrInfoExWide

class WindowsIPv4UDPResolutionFeature : IPv4UDPResolutionFeature(), CheckedImplementation {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeGetAddrInfoExWide != null && nativeFreeAddrInfoExWide != null
	override fun resolve(
		hostName: String,
		serviceName: String,
		vararg features: ResolutionFeatureIdentifier
	): List<ResolutionDataIdentifier> = winResolve(
		hostName,
		serviceName,
		AF_INET,
		SOCK_DGRAM,
		IPPROTO_UDP,
		*features
	)
}