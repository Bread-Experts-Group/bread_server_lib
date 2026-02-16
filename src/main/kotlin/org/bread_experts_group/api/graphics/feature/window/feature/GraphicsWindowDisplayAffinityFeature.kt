package org.bread_experts_group.api.graphics.feature.window.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.graphics.feature.window.feature.display_affinity.GraphicsWindowDisplayAffinityGetData
import org.bread_experts_group.api.graphics.feature.window.feature.display_affinity.GraphicsWindowDisplayAffinityGetFeature
import org.bread_experts_group.api.graphics.feature.window.feature.display_affinity.GraphicsWindowDisplayAffinitySetData
import org.bread_experts_group.api.graphics.feature.window.feature.display_affinity.GraphicsWindowDisplayAffinitySetFeature

abstract class GraphicsWindowDisplayAffinityFeature
	: GraphicsWindowFeatureImplementation<GraphicsWindowDisplayAffinityFeature>() {
	final override val expresses: FeatureExpression<GraphicsWindowDisplayAffinityFeature> =
		GraphicsWindowFeatures.WINDOW_DISPLAY_AFFINITY

	abstract fun get(
		vararg features: GraphicsWindowDisplayAffinityGetFeature
	): List<GraphicsWindowDisplayAffinityGetData>

	abstract fun set(
		vararg features: GraphicsWindowDisplayAffinitySetFeature
	): List<GraphicsWindowDisplayAffinitySetData>
}