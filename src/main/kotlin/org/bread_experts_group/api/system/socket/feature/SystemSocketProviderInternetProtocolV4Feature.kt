@file:Suppress("LongLine")

package org.bread_experts_group.api.system.socket.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.system.socket.SystemSocketProviderFeatures
import org.bread_experts_group.api.system.socket.ipv4.feature.SystemInternetProtocolV4SocketProviderFeatureImplementation
import java.util.logging.Logger

abstract class SystemSocketProviderInternetProtocolV4Feature :
	SystemSocketProviderFeatureImplementation<SystemSocketProviderInternetProtocolV4Feature>(),
	FeatureProvider<SystemInternetProtocolV4SocketProviderFeatureImplementation<*>> {
	override val expresses: FeatureExpression<SystemSocketProviderInternetProtocolV4Feature> =
		SystemSocketProviderFeatures.INTERNET_PROTOCOL_V4

	override val logger: Logger
		get() = TODO("Not yet implemented")
	override val supportedFeatures: MutableMap<
			FeatureExpression<out SystemInternetProtocolV4SocketProviderFeatureImplementation<*>>,
			MutableList<SystemInternetProtocolV4SocketProviderFeatureImplementation<*>>> = mutableMapOf()
}