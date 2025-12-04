package org.bread_experts_group.api.system.socket.ipv6.datagram.udp.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.ipv6.datagram.udp.IPv6UDPFeatures
import org.bread_experts_group.api.system.socket.resolution.ResolutionProvidingFeature

abstract class IPv6UDPResolutionFeature : IPv6UDPFeatureImplementation<IPv6UDPResolutionFeature>(),
	ResolutionProvidingFeature {
	override val expresses: FeatureExpression<IPv6UDPResolutionFeature> = IPv6UDPFeatures.NAME_RESOLUTION
}