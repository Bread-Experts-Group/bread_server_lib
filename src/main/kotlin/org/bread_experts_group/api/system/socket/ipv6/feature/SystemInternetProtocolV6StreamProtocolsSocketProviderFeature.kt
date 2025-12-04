@file:Suppress("LongLine")

package org.bread_experts_group.api.system.socket.ipv6.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.system.socket.ipv6.SystemInternetProtocolV6SocketProviderFeatures
import org.bread_experts_group.api.system.socket.ipv6.stream.feature.SystemInternetProtocolV6StreamProtocolFeatureImplementation
import java.util.logging.Logger

abstract class SystemInternetProtocolV6StreamProtocolsSocketProviderFeature :
	SystemInternetProtocolV6SocketProviderFeatureImplementation
	<SystemInternetProtocolV6StreamProtocolsSocketProviderFeature>(),
	FeatureProvider<SystemInternetProtocolV6StreamProtocolFeatureImplementation<*>> {
	override val expresses: FeatureExpression<SystemInternetProtocolV6StreamProtocolsSocketProviderFeature> =
		SystemInternetProtocolV6SocketProviderFeatures.STREAM_PROTOCOLS

	override val logger: Logger
		get() = TODO("Not yet implemented")
	override val supportedFeatures: MutableMap<
			FeatureExpression<out SystemInternetProtocolV6StreamProtocolFeatureImplementation<*>>,
			MutableList<SystemInternetProtocolV6StreamProtocolFeatureImplementation<*>>> =
		mutableMapOf()
}