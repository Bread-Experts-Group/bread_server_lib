@file:Suppress("LongLine")

package org.bread_experts_group.api.system.socket.ipv4

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.ipv4.feature.SystemInternetProtocolV4StreamProtocolsSocketProviderFeature

object SystemInternetProtocolV4SocketProviderFeatures {
	val STREAM_PROTOCOLS = object : FeatureExpression<SystemInternetProtocolV4StreamProtocolsSocketProviderFeature> {
		override val name: String = "Connection-oriented stream protocols"
	}
}