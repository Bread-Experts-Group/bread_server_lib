@file:Suppress("LongLine")

package org.bread_experts_group.api.system.socket.ipv4.stream

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.ipv4.stream.feature.SystemInternetProtocolV4TCPFeature

object SystemInternetProtocolV4StreamProtocolFeatures {
	val TRANSMISSION_CONTROL_PROTOCOL = object : FeatureExpression<SystemInternetProtocolV4TCPFeature> {
		override val name: String = "Transmission Control Protocol"
	}
}