package org.bread_experts_group.api.system.socket.ipv6.datagram

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.ipv6.datagram.feature.SystemInternetProtocolV6UDPFeature

object SystemInternetProtocolV6DatagramProtocolFeatures {
	val USER_DATAGRAM_PROTOCOL = object : FeatureExpression<SystemInternetProtocolV6UDPFeature> {
		override val name: String = "User Datagram Protocol"
	}
}