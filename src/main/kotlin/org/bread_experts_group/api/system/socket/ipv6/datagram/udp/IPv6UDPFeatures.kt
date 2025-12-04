package org.bread_experts_group.api.system.socket.ipv6.datagram.udp

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.ipv6.datagram.udp.feature.IPv6UDPResolutionFeature
import org.bread_experts_group.api.system.socket.ipv6.datagram.udp.feature.IPv6UDPSocketFeature
import org.bread_experts_group.api.system.socket.ipv6.datagram.udp.feature.IPv6UDPSystemLabelFeature

object IPv6UDPFeatures {
	val NAME_RESOLUTION = object : FeatureExpression<IPv6UDPResolutionFeature> {
		override val name: String = "UDP/IPv6 Host-Name Resolution"
	}

	val SYSTEM_LABEL = object : FeatureExpression<IPv6UDPSystemLabelFeature> {
		override val name: String = "UDP/IPv6 Provider System Label"
	}

	val SOCKET = object : FeatureExpression<IPv6UDPSocketFeature> {
		override val name: String = "UDP/IPv6 Provider Sockets"
	}
}