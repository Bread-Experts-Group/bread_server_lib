package org.bread_experts_group.api.graphics.feature.window.feature.status

import org.bread_experts_group.api.graphics.feature.window.open.GraphicsWindowOpenFeatureIdentifier

enum class StandardGraphicsWindowStatus : GraphicsWindowStatusSetFeature, GraphicsWindowStatusGetData,
	GraphicsWindowOpenFeatureIdentifier {
	SHOWN,
	HIDDEN,
	MAXIMIZED,
	MINIMIZED
}