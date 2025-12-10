package org.bread_experts_group.api.system.socket.ipv4.stream.tcp.feature.posix

import org.bread_experts_group.api.feature.CheckedImplementation
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.feature.linux.LinuxSystemNetworkingSocketsFeature.Companion.AF_INET
import org.bread_experts_group.api.system.feature.linux.LinuxSystemNetworkingSocketsFeature.Companion.IPPROTO_TCP
import org.bread_experts_group.api.system.feature.linux.LinuxSystemNetworkingSocketsFeature.Companion.SOCK_STREAM
import org.bread_experts_group.api.system.socket.ipv4.stream.tcp.feature.IPv4TCPResolutionFeature
import org.bread_experts_group.api.system.socket.resolution.ResolutionDataIdentifier
import org.bread_experts_group.api.system.socket.resolution.ResolutionFeatureIdentifier
import org.bread_experts_group.api.system.socket.system.posix.posixResolve
import org.bread_experts_group.ffi.posix.nativeGetAddrInfo

class POSIXIPv4TCPResolutionFeature : IPv4TCPResolutionFeature(), CheckedImplementation {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeGetAddrInfo != null
	override fun resolve(
		hostName: String,
		serviceName: String,
		vararg features: ResolutionFeatureIdentifier
	): List<ResolutionDataIdentifier> = posixResolve(
		hostName,
		serviceName,
		AF_INET,
		SOCK_STREAM,
		IPPROTO_TCP,
		*features
	)
}