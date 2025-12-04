package org.bread_experts_group.api.system.socket.ipv6

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.ipv6.feature.SystemInternetProtocolV6DatagramProtocolsSocketProviderFeature
import org.bread_experts_group.api.system.socket.ipv6.feature.SystemInternetProtocolV6StreamProtocolsSocketProviderFeature

object SystemInternetProtocolV6SocketProviderFeatures {
	val STREAM_PROTOCOLS = object : FeatureExpression<SystemInternetProtocolV6StreamProtocolsSocketProviderFeature> {
		override val name: String = "Connection-oriented stream protocols"
	}

	val DATAGRAM_PROTOCOLS =
		object : FeatureExpression<SystemInternetProtocolV6DatagramProtocolsSocketProviderFeature> {
			override val name: String = "Connection-less datagram protocols"
		}
}