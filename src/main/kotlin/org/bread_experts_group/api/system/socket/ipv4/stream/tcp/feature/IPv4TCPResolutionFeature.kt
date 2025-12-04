package org.bread_experts_group.api.system.socket.ipv4.stream.tcp.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.ipv4.stream.tcp.IPv4TCPFeatures
import org.bread_experts_group.api.system.socket.resolution.ResolutionProvidingFeature

abstract class IPv4TCPResolutionFeature : IPv4TCPFeatureImplementation<IPv4TCPResolutionFeature>(),
	ResolutionProvidingFeature {
	override val expresses: FeatureExpression<IPv4TCPResolutionFeature> = IPv4TCPFeatures.NAME_RESOLUTION
}