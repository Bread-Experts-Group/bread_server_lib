package org.bread_experts_group.api.graphics

import org.bread_experts_group.api.apiRootLogger
import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.generic.logging.LevelLogger

object GraphicsProvider : FeatureProvider<GraphicsFeatureImplementation<*>> {
	override val logger = LevelLogger("graphics", apiRootLogger)
	override val features: MutableList<GraphicsFeatureImplementation<*>> = mutableListOf()
	override val supportedFeatures: MutableMap<
			FeatureExpression<out GraphicsFeatureImplementation<*>>,
			MutableList<GraphicsFeatureImplementation<*>>
			> = mutableMapOf()
}