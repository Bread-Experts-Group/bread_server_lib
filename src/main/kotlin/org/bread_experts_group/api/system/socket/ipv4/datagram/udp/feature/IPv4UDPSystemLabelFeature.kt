package org.bread_experts_group.api.system.socket.ipv4.datagram.udp.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.socket.ipv4.datagram.udp.IPv4UDPFeatures

data class IPv4UDPSystemLabelFeature(
	override val source: ImplementationSource,
	val label: String
) : IPv4UDPFeatureImplementation<IPv4UDPSystemLabelFeature>() {
	override val expresses: FeatureExpression<IPv4UDPSystemLabelFeature> = IPv4UDPFeatures.SYSTEM_LABEL
}