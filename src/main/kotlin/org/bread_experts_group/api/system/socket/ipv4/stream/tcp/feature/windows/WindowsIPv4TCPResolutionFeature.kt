package org.bread_experts_group.api.system.socket.ipv4.stream.tcp.feature.windows

import org.bread_experts_group.api.feature.CheckedImplementation
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.AF_INET
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.IPPROTO_TCP
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.SOCK_STREAM
import org.bread_experts_group.api.system.socket.ipv4.stream.tcp.feature.IPv4TCPResolutionFeature
import org.bread_experts_group.api.system.socket.resolution.ResolutionDataIdentifier
import org.bread_experts_group.api.system.socket.resolution.ResolutionFeatureIdentifier
import org.bread_experts_group.api.system.socket.system.windows.winResolve
import org.bread_experts_group.ffi.windows.wsa.nativeFreeAddrInfoExWide
import org.bread_experts_group.ffi.windows.wsa.nativeGetAddrInfoExWide

class WindowsIPv4TCPResolutionFeature : IPv4TCPResolutionFeature(), CheckedImplementation {
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
		SOCK_STREAM,
		IPPROTO_TCP,
		*features
	)
}