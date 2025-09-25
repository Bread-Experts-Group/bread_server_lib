package org.bread_experts_group.api.graphics.feature.window.feature

import org.bread_experts_group.api.FeatureExpression

abstract class GraphicsWindowResizeEventFeature :
	GraphicsWindowFeatureImplementation<GraphicsWindowResizeEventFeature>() {
	final override val expresses: FeatureExpression<GraphicsWindowResizeEventFeature> =
		GraphicsWindowFeatures.RESIZE_LAMBDA
	var lambda: (w: Long, h: Long) -> Unit = { _, _ -> }
}