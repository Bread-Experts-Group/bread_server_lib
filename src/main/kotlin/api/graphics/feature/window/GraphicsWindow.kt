package org.bread_experts_group.api.graphics.feature.window

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.FeatureProvider
import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.PreInitializableClosable
import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowFeatureImplementation
import java.util.concurrent.Semaphore

abstract class GraphicsWindow : FeatureProvider<GraphicsWindowFeatureImplementation<*>>, PreInitializableClosable {
	abstract val features: Set<GraphicsWindowFeatureImplementation<*>>
	override fun <I : GraphicsWindowFeatureImplementation<*>, E : FeatureExpression<I>> get(
		feature: E,
		allowEmulated: Boolean
	): I? {
		val found = features.firstOrNull {
			it.expresses == feature &&
					(if (allowEmulated) true else it.source != ImplementationSource.JVM_EMULATED)
		} ?: return null
		@Suppress("UNCHECKED_CAST")
		return found as I
	}

	val processingLock: Semaphore = Semaphore(1)
}