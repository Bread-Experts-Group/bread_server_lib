package org.bread_experts_group.api.graphics.feature.console.feature.device

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.graphics.GraphicsProvider
import org.bread_experts_group.api.graphics.feature.console.feature.device.feature.GraphicsConsoleIOFeatureImplementation
import org.bread_experts_group.generic.logging.LevelLogger
import java.util.*

abstract class GraphicsConsoleIOFeature : GraphicsConsoleFeatureImplementation<GraphicsConsoleIOFeature>(),
	FeatureProvider<GraphicsConsoleIOFeatureImplementation<*>> {
	override val logger = LevelLogger("console i/o", GraphicsProvider.logger)
	override val supportedFeatures: MutableMap<
			FeatureExpression<out GraphicsConsoleIOFeatureImplementation<*>>,
			MutableList<GraphicsConsoleIOFeatureImplementation<*>>> = mutableMapOf()
	override val features: MutableList<GraphicsConsoleIOFeatureImplementation<*>> = ServiceLoader.load(
		GraphicsConsoleIOFeatureImplementation::class.java
	).toMutableList()
}