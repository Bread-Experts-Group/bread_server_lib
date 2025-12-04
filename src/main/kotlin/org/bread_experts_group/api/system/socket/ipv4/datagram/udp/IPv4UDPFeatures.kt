package org.bread_experts_group.api.system.socket.ipv4.datagram.udp

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.ipv4.datagram.udp.feature.IPv4UDPResolutionFeature
import org.bread_experts_group.api.system.socket.ipv4.datagram.udp.feature.IPv4UDPSocketFeature
import org.bread_experts_group.api.system.socket.ipv4.datagram.udp.feature.IPv4UDPSystemLabelFeature

object IPv4UDPFeatures {
	val NAME_RESOLUTION = object : FeatureExpression<IPv4UDPResolutionFeature> {
		override val name: String = "UDP/IPv4 Host-Name Resolution"
	}

	val SYSTEM_LABEL = object : FeatureExpression<IPv4UDPSystemLabelFeature> {
		override val name: String = "UDP/IPv4 Provider System Label"
	}

	val SOCKET = object : FeatureExpression<IPv4UDPSocketFeature> {
		override val name: String = "UDP/IPv4 Provider Sockets"
	}
}