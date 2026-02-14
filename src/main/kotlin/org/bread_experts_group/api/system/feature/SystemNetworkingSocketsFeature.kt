package org.bread_experts_group.api.system.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.api.system.socket.sys_feature.SystemSocketProviderFeatureImplementation
import org.bread_experts_group.generic.logging.LevelLogger

abstract class SystemNetworkingSocketsFeature :
	SystemFeatureImplementation<SystemNetworkingSocketsFeature>(),
	FeatureProvider<SystemSocketProviderFeatureImplementation<*>> {
	override val expresses: FeatureExpression<SystemNetworkingSocketsFeature> =
		SystemFeatures.NETWORKING_SOCKETS

	override val logger = LevelLogger("networking", SystemProvider.logger)
	override val supportedFeatures: MutableMap<
			FeatureExpression<out SystemSocketProviderFeatureImplementation<*>>,
			MutableList<SystemSocketProviderFeatureImplementation<*>>> = mutableMapOf()
}