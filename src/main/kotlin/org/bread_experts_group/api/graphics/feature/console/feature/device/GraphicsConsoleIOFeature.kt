package org.bread_experts_group.api.graphics.feature.console.feature.device

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.graphics.feature.console.feature.device.feature.GraphicsConsoleIOFeatureImplementation
import org.bread_experts_group.logging.ColoredHandler
import java.util.*
import java.util.logging.Logger

abstract class GraphicsConsoleIOFeature : GraphicsConsoleFeatureImplementation<GraphicsConsoleIOFeature>(),
	FeatureProvider<GraphicsConsoleIOFeatureImplementation<*>> {
	override val logger: Logger = ColoredHandler.newLogger("TMP logger")
	override val supportedFeatures: MutableMap<
			FeatureExpression<out GraphicsConsoleIOFeatureImplementation<*>>,
			MutableList<GraphicsConsoleIOFeatureImplementation<*>>> = mutableMapOf()
	override val features: MutableList<GraphicsConsoleIOFeatureImplementation<*>> = ServiceLoader.load(
		GraphicsConsoleIOFeatureImplementation::class.java
	).toMutableList()
}