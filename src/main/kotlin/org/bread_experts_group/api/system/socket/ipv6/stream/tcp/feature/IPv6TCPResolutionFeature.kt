package org.bread_experts_group.api.system.socket.ipv6.stream.tcp.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.ipv6.stream.tcp.IPv6TCPFeatures
import org.bread_experts_group.api.system.socket.resolution.ResolutionProvidingFeature

abstract class IPv6TCPResolutionFeature : IPv6TCPFeatureImplementation<IPv6TCPResolutionFeature>(),
	ResolutionProvidingFeature {
	override val expresses: FeatureExpression<IPv6TCPResolutionFeature> = IPv6TCPFeatures.NAME_RESOLUTION
}