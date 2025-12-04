package org.bread_experts_group.api.system.socket.ipv4.stream.tcp

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.ipv4.stream.tcp.feature.IPv4TCPResolutionFeature
import org.bread_experts_group.api.system.socket.ipv4.stream.tcp.feature.IPv4TCPSocketFeature
import org.bread_experts_group.api.system.socket.ipv4.stream.tcp.feature.IPv4TCPSystemLabelFeature

object IPv4TCPFeatures {
	val NAME_RESOLUTION = object : FeatureExpression<IPv4TCPResolutionFeature> {
		override val name: String = "TCP/IPv4 Host-Name Resolution"
	}

	val SYSTEM_LABEL = object : FeatureExpression<IPv4TCPSystemLabelFeature> {
		override val name: String = "TCP/IPv4 Provider System Label"
	}

	val SOCKET = object : FeatureExpression<IPv4TCPSocketFeature> {
		override val name: String = "TCP/IPv4 Provider Sockets"
	}
}