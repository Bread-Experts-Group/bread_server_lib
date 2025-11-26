package org.bread_experts_group.api.system.socket.ipv4.stream.tcp.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.ipv4.stream.tcp.IPV4TCPFeatures
import org.bread_experts_group.api.system.socket.resolution.ResolutionProvidingFeature

abstract class IPV4TCPResolutionFeature : IPV4TCPFeatureImplementation<IPV4TCPResolutionFeature>(),
	ResolutionProvidingFeature {
	override val expresses: FeatureExpression<IPV4TCPResolutionFeature> = IPV4TCPFeatures.NAME_RESOLUTION
}