package org.bread_experts_group.api.graphics.feature.window

import org.bread_experts_group.api.graphics.GraphicsFeatureImplementation
import org.bread_experts_group.api.graphics.feature.window.open.GraphicsWindowOpenDataIdentifier
import org.bread_experts_group.api.graphics.feature.window.open.GraphicsWindowOpenFeatureIdentifier

abstract class GraphicsWindowFeature : GraphicsFeatureImplementation<GraphicsWindowFeature>() {
	abstract fun open(vararg features: GraphicsWindowOpenFeatureIdentifier): List<GraphicsWindowOpenDataIdentifier>
}