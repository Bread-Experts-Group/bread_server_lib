package org.bread_experts_group.api.system.socket.ipv6.stream.tcp.feature.windows

import org.bread_experts_group.api.feature.CheckedImplementation
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.AF_INET6
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.IPPROTO_TCP
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.SOCK_STREAM
import org.bread_experts_group.api.system.socket.ipv4.windows.winResolve
import org.bread_experts_group.api.system.socket.ipv6.stream.tcp.feature.IPv6TCPResolutionFeature
import org.bread_experts_group.api.system.socket.resolution.ResolutionDataIdentifier
import org.bread_experts_group.api.system.socket.resolution.ResolutionFeatureIdentifier
import org.bread_experts_group.ffi.windows.wsa.nativeFreeAddrInfoExW
import org.bread_experts_group.ffi.windows.wsa.nativeGetAddrInfoExW

class WindowsIPv6TCPResolutionFeature : IPv6TCPResolutionFeature(), CheckedImplementation {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeGetAddrInfoExW != null && nativeFreeAddrInfoExW != null
	override fun resolve(
		hostName: String,
		serviceName: String,
		vararg features: ResolutionFeatureIdentifier
	): List<ResolutionDataIdentifier> = winResolve(
		hostName,
		serviceName,
		AF_INET6,
		SOCK_STREAM,
		IPPROTO_TCP,
		*features
	)
}