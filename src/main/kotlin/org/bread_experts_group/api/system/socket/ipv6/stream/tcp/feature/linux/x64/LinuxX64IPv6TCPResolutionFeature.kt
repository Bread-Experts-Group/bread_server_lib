package org.bread_experts_group.api.system.socket.ipv6.stream.tcp.feature.linux.x64

import org.bread_experts_group.api.feature.CheckedImplementation
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.socket.ipv6.stream.tcp.feature.IPv6TCPResolutionFeature
import org.bread_experts_group.api.system.socket.resolution.ResolutionDataIdentifier
import org.bread_experts_group.api.system.socket.resolution.ResolutionFeatureIdentifier

class LinuxX64IPv6TCPResolutionFeature : IPv6TCPResolutionFeature(), CheckedImplementation {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = true
	override fun resolve(
		hostName: String,
		serviceName: String,
		vararg features: ResolutionFeatureIdentifier
	): List<ResolutionDataIdentifier> = TODO("Resolve") /*winResolve(
		hostName,
		serviceName,
		AF_INET6,
		SOCK_DGRAM,
		IPPROTO_UDP,
		*features
	)*/
}