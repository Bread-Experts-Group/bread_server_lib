package org.bread_experts_group.api.graphics.feature.window.feature

import org.bread_experts_group.api.FeatureExpression

abstract class GraphicsWindowNameFeature : GraphicsWindowFeatureImplementation<GraphicsWindowNameFeature>() {
	final override val expresses: FeatureExpression<GraphicsWindowNameFeature> = GraphicsWindowFeatures.WINDOW_NAME
	abstract var name: String
}