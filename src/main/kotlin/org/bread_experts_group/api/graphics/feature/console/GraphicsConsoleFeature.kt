package org.bread_experts_group.api.graphics.feature.console

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.graphics.GraphicsFeatureImplementation
import org.bread_experts_group.api.graphics.GraphicsFeatures
import org.bread_experts_group.api.graphics.GraphicsProvider
import org.bread_experts_group.api.graphics.feature.console.feature.device.GraphicsConsoleFeatureImplementation
import org.bread_experts_group.generic.logging.LevelLogger
import java.util.*

abstract class GraphicsConsoleFeature : GraphicsFeatureImplementation<GraphicsConsoleFeature>(),
	FeatureProvider<GraphicsConsoleFeatureImplementation<*>> {
	override val expresses: FeatureExpression<GraphicsConsoleFeature> = GraphicsFeatures.CUI_CONSOLE
	override val logger = LevelLogger("console", GraphicsProvider.logger)
	override val features: MutableList<GraphicsConsoleFeatureImplementation<*>> = ServiceLoader.load(
		GraphicsConsoleFeatureImplementation::class.java
	).toMutableList()
	override val supportedFeatures: MutableMap<
			FeatureExpression<out GraphicsConsoleFeatureImplementation<*>>,
			MutableList<GraphicsConsoleFeatureImplementation<*>>> = mutableMapOf()
}