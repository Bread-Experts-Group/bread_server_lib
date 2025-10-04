package org.bread_experts_group.api.graphics

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.FeatureProvider
import org.bread_experts_group.api.graphics.feature.GraphicsFeatureImplementation
import org.bread_experts_group.logging.ColoredHandler
import java.util.*
import java.util.logging.Logger

object GraphicsProvider : FeatureProvider<GraphicsFeatureImplementation<*>> {
	override val logger: Logger = ColoredHandler.newLogger("TMP logger")
	override val features: MutableList<GraphicsFeatureImplementation<*>> = ServiceLoader.load(
		GraphicsFeatureImplementation::class.java
	).toMutableList()
	override val supportedFeatures: MutableMap<
			FeatureExpression<out GraphicsFeatureImplementation<*>>,
			MutableList<GraphicsFeatureImplementation<*>>
			> = mutableMapOf()
}