package org.bread_experts_group.api.graphics.feature.window.feature

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.PreInitializableClosable

abstract class GraphicsWindowDirectXContext :
	GraphicsWindowFeatureImplementation<GraphicsWindowDirectXContext>(), PreInitializableClosable {
	final override val expresses: FeatureExpression<GraphicsWindowDirectXContext> =
		GraphicsWindowFeatures.DIRECTX_CONTEXT
	var use: Boolean = false
}