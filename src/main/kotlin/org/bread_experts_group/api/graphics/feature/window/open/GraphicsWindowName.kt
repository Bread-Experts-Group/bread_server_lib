package org.bread_experts_group.api.graphics.feature.window.open

import org.bread_experts_group.api.graphics.feature.window.feature.name.GraphicsWindowNameGetData
import org.bread_experts_group.api.graphics.feature.window.feature.name.GraphicsWindowNameSetFeature

data class GraphicsWindowName(
	val name: String
) : GraphicsWindowOpenFeatureIdentifier, GraphicsWindowNameGetData, GraphicsWindowNameSetFeature