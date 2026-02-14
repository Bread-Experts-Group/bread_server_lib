package org.bread_experts_group.api.system.user

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.api.system.user.feature.SystemUserFeatureImplementation
import org.bread_experts_group.generic.logging.LevelLogger

abstract class SystemUser : FeatureProvider<SystemUserFeatureImplementation<*>> {
	override val logger = LevelLogger("user", SystemProvider.logger)
	override val supportedFeatures: MutableMap<
			FeatureExpression<out SystemUserFeatureImplementation<*>>,
			MutableList<SystemUserFeatureImplementation<*>>> = mutableMapOf()
}