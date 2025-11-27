@file:Suppress("LongLine")

package org.bread_experts_group.api.system.socket.sys_feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.system.socket.SystemSocketProviderFeatures
import org.bread_experts_group.api.system.socket.ipv6.feature.SystemInternetProtocolV6SocketProviderFeatureImplementation
import java.util.logging.Logger

abstract class SystemSocketProviderInternetProtocolV6Feature :
	SystemSocketProviderFeatureImplementation<SystemSocketProviderInternetProtocolV6Feature>(),
	FeatureProvider<SystemInternetProtocolV6SocketProviderFeatureImplementation<*>> {
	override val expresses: FeatureExpression<SystemSocketProviderInternetProtocolV6Feature> =
		SystemSocketProviderFeatures.INTERNET_PROTOCOL_V6

	override val logger: Logger
		get() = TODO("Not yet implemented")
	override val supportedFeatures: MutableMap<
			FeatureExpression<out SystemInternetProtocolV6SocketProviderFeatureImplementation<*>>,
			MutableList<SystemInternetProtocolV6SocketProviderFeatureImplementation<*>>> = mutableMapOf()
}