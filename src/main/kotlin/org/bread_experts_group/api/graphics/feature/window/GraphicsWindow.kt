package org.bread_experts_group.api.graphics.feature.window

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.FeatureProvider
import org.bread_experts_group.api.PreInitializableClosable
import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowFeatureImplementation
import org.bread_experts_group.logging.ColoredHandler
import java.util.*
import java.util.concurrent.Semaphore
import java.util.logging.Logger

abstract class GraphicsWindow : FeatureProvider<GraphicsWindowFeatureImplementation<*>>, PreInitializableClosable {
	override val logger: Logger = ColoredHandler.newLogger("TMP logger")
	override val features: MutableList<GraphicsWindowFeatureImplementation<*>> = ServiceLoader.load(
		GraphicsWindowFeatureImplementation::class.java
	).toMutableList()
	override val supportedFeatures: MutableMap<
			FeatureExpression<out GraphicsWindowFeatureImplementation<*>>,
			MutableList<GraphicsWindowFeatureImplementation<*>>
			> = mutableMapOf()

	val processingLock: Semaphore = Semaphore(1)
}