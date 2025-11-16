package org.bread_experts_group.api.system

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.system.feature.SystemFeatureImplementation
import org.bread_experts_group.logging.ColoredHandler
import java.util.*
import java.util.logging.Logger

object SystemProvider : FeatureProvider<SystemFeatureImplementation<*>> {
	override val logger: Logger = ColoredHandler.newLogger("TMP logger")
	override val features: MutableList<SystemFeatureImplementation<*>> = ServiceLoader.load(
		SystemFeatureImplementation::class.java
	).toMutableList()
	override val supportedFeatures: MutableMap<
			FeatureExpression<out SystemFeatureImplementation<*>>,
			MutableList<SystemFeatureImplementation<*>>> = mutableMapOf()
}