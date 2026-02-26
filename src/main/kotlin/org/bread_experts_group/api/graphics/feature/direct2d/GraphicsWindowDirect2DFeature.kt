package org.bread_experts_group.api.graphics.feature.direct2d

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.graphics.GraphicsFeatureImplementation
import org.bread_experts_group.api.graphics.GraphicsFeatures
import org.bread_experts_group.api.graphics.feature.direct2d.factory.GraphicsWindowDirect2DFactoryData
import org.bread_experts_group.api.graphics.feature.direct2d.factory.GraphicsWindowDirect2DFactoryFeature

abstract class GraphicsWindowDirect2DFeature : GraphicsFeatureImplementation<GraphicsWindowDirect2DFeature>() {
	override val expresses: FeatureExpression<GraphicsWindowDirect2DFeature> = GraphicsFeatures.DIRECT2D
	abstract fun factory(vararg features: GraphicsWindowDirect2DFactoryFeature): List<GraphicsWindowDirect2DFactoryData>
}