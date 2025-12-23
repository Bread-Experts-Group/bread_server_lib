package org.bread_experts_group.api.system.socket.ipv6.datagram.udp.feature.linux.x64

import org.bread_experts_group.api.feature.CheckedImplementation
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.socket.ipv6.datagram.udp.feature.IPv6UDPResolutionFeature
import org.bread_experts_group.api.system.socket.resolution.ResolutionDataIdentifier
import org.bread_experts_group.api.system.socket.resolution.ResolutionFeatureIdentifier

class LinuxX64IPv6UDPResolutionFeature : IPv6UDPResolutionFeature(), CheckedImplementation {
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