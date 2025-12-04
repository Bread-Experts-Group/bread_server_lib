package org.bread_experts_group.api.system.socket.ipv4.stream.tcp.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.socket.ipv4.stream.tcp.IPv4TCPFeatures

data class IPv4TCPSystemLabelFeature(
	override val source: ImplementationSource,
	val label: String
) : IPv4TCPFeatureImplementation<IPv4TCPSystemLabelFeature>() {
	override val expresses: FeatureExpression<IPv4TCPSystemLabelFeature> = IPv4TCPFeatures.SYSTEM_LABEL
}