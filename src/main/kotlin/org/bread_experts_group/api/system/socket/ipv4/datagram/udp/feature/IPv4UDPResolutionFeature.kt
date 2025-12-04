package org.bread_experts_group.api.system.socket.ipv4.datagram.udp.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.ipv4.datagram.udp.IPv4UDPFeatures
import org.bread_experts_group.api.system.socket.resolution.ResolutionProvidingFeature

abstract class IPv4UDPResolutionFeature : IPv4UDPFeatureImplementation<IPv4UDPResolutionFeature>(),
	ResolutionProvidingFeature {
	override val expresses: FeatureExpression<IPv4UDPResolutionFeature> = IPv4UDPFeatures.NAME_RESOLUTION
}