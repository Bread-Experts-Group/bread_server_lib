package org.bread_experts_group.api.graphics.feature.directwrite

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.graphics.GraphicsFeatureImplementation
import org.bread_experts_group.api.graphics.GraphicsFeatures
import org.bread_experts_group.api.graphics.feature.directwrite.factory.GraphicsWindowDirectWriteFactoryData
import org.bread_experts_group.api.graphics.feature.directwrite.factory.GraphicsWindowDirectWriteFactoryFeature

abstract class GraphicsWindowDirectWriteFeature : GraphicsFeatureImplementation<GraphicsWindowDirectWriteFeature>() {
	override val expresses: FeatureExpression<GraphicsWindowDirectWriteFeature> = GraphicsFeatures.DIRECTWRITE
	abstract fun factory(
		vararg features: GraphicsWindowDirectWriteFactoryFeature
	): List<GraphicsWindowDirectWriteFactoryData>
}