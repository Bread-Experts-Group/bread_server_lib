package org.bread_experts_group.api.system.socket.ipv6.stream.tcp

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.ipv6.stream.tcp.feature.IPv6TCPResolutionFeature
import org.bread_experts_group.api.system.socket.ipv6.stream.tcp.feature.IPv6TCPSocketFeature
import org.bread_experts_group.api.system.socket.ipv6.stream.tcp.feature.IPv6TCPSystemLabelFeature

object IPv6TCPFeatures {
	val NAME_RESOLUTION = object : FeatureExpression<IPv6TCPResolutionFeature> {
		override val name: String = "TCP/IPv6 Host-Name Resolution"
	}

	val SYSTEM_LABEL = object : FeatureExpression<IPv6TCPSystemLabelFeature> {
		override val name: String = "TCP/IPv6 Provider System Label"
	}

	val SOCKET = object : FeatureExpression<IPv6TCPSocketFeature> {
		override val name: String = "TCP/IPv6 Provider Sockets"
	}
}