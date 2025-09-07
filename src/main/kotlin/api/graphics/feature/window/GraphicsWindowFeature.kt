package org.bread_experts_group.api.graphics.feature.window

import org.bread_experts_group.api.graphics.feature.GraphicsFeatureImplementation

abstract class GraphicsWindowFeature : GraphicsFeatureImplementation<GraphicsWindowFeature>() {
	abstract fun createTemplate(): GraphicsWindowTemplate
	abstract fun createWindow(template: GraphicsWindowTemplate): GraphicsWindow
}