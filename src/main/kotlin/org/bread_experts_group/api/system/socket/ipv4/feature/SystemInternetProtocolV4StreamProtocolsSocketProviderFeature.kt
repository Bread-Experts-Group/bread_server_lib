@file:Suppress("LongLine")

package org.bread_experts_group.api.system.socket.ipv4.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.system.socket.ipv4.SystemInternetProtocolV4SocketProviderFeatures
import org.bread_experts_group.api.system.socket.ipv4.stream.feature.SystemInternetProtocolV4StreamProtocolFeatureImplementation

abstract class SystemInternetProtocolV4StreamProtocolsSocketProviderFeature :
	SystemInternetProtocolV4SocketProviderFeatureImplementation
	<SystemInternetProtocolV4StreamProtocolsSocketProviderFeature>(),
	FeatureProvider<SystemInternetProtocolV4StreamProtocolFeatureImplementation<*>> {
	override val expresses: FeatureExpression<SystemInternetProtocolV4StreamProtocolsSocketProviderFeature> =
		SystemInternetProtocolV4SocketProviderFeatures.STREAM_PROTOCOLS

	override val logger
		get() = TODO("Not yet implemented")
	override val supportedFeatures: MutableMap<
			FeatureExpression<out SystemInternetProtocolV4StreamProtocolFeatureImplementation<*>>,
			MutableList<SystemInternetProtocolV4StreamProtocolFeatureImplementation<*>>> =
		mutableMapOf()
}