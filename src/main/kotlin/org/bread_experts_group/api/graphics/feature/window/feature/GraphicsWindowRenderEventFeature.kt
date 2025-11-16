package org.bread_experts_group.api.graphics.feature.window.feature

import org.bread_experts_group.api.feature.FeatureExpression

abstract class GraphicsWindowRenderEventFeature :
	GraphicsWindowFeatureImplementation<GraphicsWindowRenderEventFeature>() {
	final override val expresses: FeatureExpression<GraphicsWindowRenderEventFeature> =
		GraphicsWindowFeatures.RENDER_LAMBDA
	var lambda: () -> Unit = {}
}