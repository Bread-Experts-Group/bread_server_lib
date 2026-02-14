package org.bread_experts_group.api.graphics.feature.window.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.graphics.feature.window.feature.name.GraphicsWindowNameGetData
import org.bread_experts_group.api.graphics.feature.window.feature.name.GraphicsWindowNameGetFeature
import org.bread_experts_group.api.graphics.feature.window.feature.name.GraphicsWindowNameSetData
import org.bread_experts_group.api.graphics.feature.window.feature.name.GraphicsWindowNameSetFeature

abstract class GraphicsWindowNameFeature : GraphicsWindowFeatureImplementation<GraphicsWindowNameFeature>() {
	final override val expresses: FeatureExpression<GraphicsWindowNameFeature> = GraphicsWindowFeatures.WINDOW_NAME
	abstract fun get(vararg features: GraphicsWindowNameGetFeature): List<GraphicsWindowNameGetData>
	abstract fun set(vararg features: GraphicsWindowNameSetFeature): List<GraphicsWindowNameSetData>
}