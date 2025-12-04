package org.bread_experts_group.api.system.socket.ipv6.datagram.udp.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.socket.ipv6.datagram.udp.IPv6UDPFeatures

data class IPv6UDPSystemLabelFeature(
	override val source: ImplementationSource,
	val label: String
) : IPv6UDPFeatureImplementation<IPv6UDPSystemLabelFeature>() {
	override val expresses: FeatureExpression<IPv6UDPSystemLabelFeature> = IPv6UDPFeatures.SYSTEM_LABEL
}