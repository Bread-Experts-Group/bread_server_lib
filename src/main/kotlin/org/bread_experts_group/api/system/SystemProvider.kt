package org.bread_experts_group.api.system

import org.bread_experts_group.api.apiRootLogger
import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.system.feature.SystemFeatureImplementation
import org.bread_experts_group.generic.logging.LevelLogger

object SystemProvider : FeatureProvider<SystemFeatureImplementation<*>> {
	override val logger = LevelLogger("system", apiRootLogger)
	override val features: MutableList<SystemFeatureImplementation<*>> = mutableListOf()
	override val supportedFeatures: MutableMap<
			FeatureExpression<out SystemFeatureImplementation<*>>,
			MutableList<SystemFeatureImplementation<*>>> = mutableMapOf()
}