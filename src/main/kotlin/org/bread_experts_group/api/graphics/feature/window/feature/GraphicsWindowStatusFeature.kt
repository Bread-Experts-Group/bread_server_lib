package org.bread_experts_group.api.graphics.feature.window.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.graphics.feature.window.feature.status.GraphicsWindowStatusGetData
import org.bread_experts_group.api.graphics.feature.window.feature.status.GraphicsWindowStatusGetFeature
import org.bread_experts_group.api.graphics.feature.window.feature.status.GraphicsWindowStatusSetData
import org.bread_experts_group.api.graphics.feature.window.feature.status.GraphicsWindowStatusSetFeature

abstract class GraphicsWindowStatusFeature : GraphicsWindowFeatureImplementation<GraphicsWindowStatusFeature>() {
	final override val expresses: FeatureExpression<GraphicsWindowStatusFeature> = GraphicsWindowFeatures.WINDOW_STATUS
	abstract fun get(vararg features: GraphicsWindowStatusGetFeature): List<GraphicsWindowStatusGetData>
	abstract fun set(vararg features: GraphicsWindowStatusSetFeature): List<GraphicsWindowStatusSetData>
}