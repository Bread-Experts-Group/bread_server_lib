package org.bread_experts_group.api.graphics.feature.window.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.GraphicsWindowEventLoopSubscriptionData
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.GraphicsWindowEventLoopSubscriptionFeature

abstract class GraphicsWindowEventLoopFeature : GraphicsWindowFeatureImplementation<GraphicsWindowEventLoopFeature>() {
	final override val expresses: FeatureExpression<GraphicsWindowEventLoopFeature> =
		GraphicsWindowFeatures.WINDOW_EVENT

	abstract fun add(
		vararg features: GraphicsWindowEventLoopSubscriptionFeature
	): List<GraphicsWindowEventLoopSubscriptionData>
}