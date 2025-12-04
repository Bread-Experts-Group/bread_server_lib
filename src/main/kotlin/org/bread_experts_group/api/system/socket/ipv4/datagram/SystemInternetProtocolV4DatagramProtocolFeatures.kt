package org.bread_experts_group.api.system.socket.ipv4.datagram

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.ipv4.datagram.feature.SystemInternetProtocolV4UDPFeature

object SystemInternetProtocolV4DatagramProtocolFeatures {
	val USER_DATAGRAM_PROTOCOL = object : FeatureExpression<SystemInternetProtocolV4UDPFeature> {
		override val name: String = "User Datagram Protocol"
	}
}