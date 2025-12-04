@file:Suppress("LongLine")

package org.bread_experts_group.api.system.socket.ipv6.stream

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.ipv6.stream.feature.SystemInternetProtocolV6TCPFeature

object SystemInternetProtocolV6StreamProtocolFeatures {
	val TRANSMISSION_CONTROL_PROTOCOL = object : FeatureExpression<SystemInternetProtocolV6TCPFeature> {
		override val name: String = "Transmission Control Protocol"
	}
}