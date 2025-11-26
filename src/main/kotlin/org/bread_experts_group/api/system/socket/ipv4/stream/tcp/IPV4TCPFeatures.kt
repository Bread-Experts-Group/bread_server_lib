package org.bread_experts_group.api.system.socket.ipv4.stream.tcp

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.ipv4.stream.tcp.feature.IPV4TCPResolutionFeature
import org.bread_experts_group.api.system.socket.ipv4.stream.tcp.feature.IPV4TCPSystemLabelFeature

object IPV4TCPFeatures {
	val NAME_RESOLUTION = object : FeatureExpression<IPV4TCPResolutionFeature> {
		override val name: String = "TCP/IPv4 Host-Name Resolution"
	}

	val SYSTEM_LABEL = object : FeatureExpression<IPV4TCPSystemLabelFeature> {
		override val name: String = "TCP/IPv4 Provider System Label"
	}
}