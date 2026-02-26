package org.bread_experts_group.api.graphics.feature.window

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.graphics.GraphicsProvider
import org.bread_experts_group.api.graphics.feature.direct2d.factory.cwrtgt.GWD2DCreateWindowRenderTargetFeature
import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowFeatureImplementation
import org.bread_experts_group.api.graphics.feature.window.open.GraphicsWindowOpenDataIdentifier
import org.bread_experts_group.generic.logging.LevelLogger

abstract class GraphicsWindow : FeatureProvider<GraphicsWindowFeatureImplementation<*>>,
	GraphicsWindowOpenDataIdentifier, GWD2DCreateWindowRenderTargetFeature {
	override val logger = LevelLogger("window", GraphicsProvider.logger)
	override val features: MutableList<GraphicsWindowFeatureImplementation<*>> = mutableListOf()
	override val supportedFeatures: MutableMap<
			FeatureExpression<out GraphicsWindowFeatureImplementation<*>>,
			MutableList<GraphicsWindowFeatureImplementation<*>>
			> = mutableMapOf()
}