package org.bread_experts_group.api.system.socket.ipv4.stream.tcp.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.socket.ipv4.stream.tcp.IPV4TCPFeatures

data class IPV4TCPSystemLabelFeature(
	override val source: ImplementationSource,
	val label: String
) : IPV4TCPFeatureImplementation<IPV4TCPSystemLabelFeature>() {
	override val expresses: FeatureExpression<IPV4TCPSystemLabelFeature> = IPV4TCPFeatures.SYSTEM_LABEL
}