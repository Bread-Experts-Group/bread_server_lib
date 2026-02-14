package org.bread_experts_group.api.graphics

import org.bread_experts_group.api.apiRootLogger
import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.generic.logging.LevelLogger
import java.util.*

object GraphicsProvider : FeatureProvider<GraphicsFeatureImplementation<*>> {
	override val logger = LevelLogger("graphics", apiRootLogger)
	override val features: MutableList<GraphicsFeatureImplementation<*>> = ServiceLoader.load(
		GraphicsFeatureImplementation::class.java
	).toMutableList()
	override val supportedFeatures: MutableMap<
			FeatureExpression<out GraphicsFeatureImplementation<*>>,
			MutableList<GraphicsFeatureImplementation<*>>
			> = mutableMapOf()
}