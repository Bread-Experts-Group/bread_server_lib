package org.bread_experts_group.api.system.socket.ipv6.stream.tcp.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.socket.ipv6.stream.tcp.IPv6TCPFeatures

data class IPv6TCPSystemLabelFeature(
	override val source: ImplementationSource,
	val label: String
) : IPv6TCPFeatureImplementation<IPv6TCPSystemLabelFeature>() {
	override val expresses: FeatureExpression<IPv6TCPSystemLabelFeature> = IPv6TCPFeatures.SYSTEM_LABEL
}