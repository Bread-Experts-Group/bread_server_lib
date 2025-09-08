package org.bread_experts_group.api.graphics.feature.window.feature

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.PreInitializableClosable

abstract class GraphicsWindowOpenGLContextFeature :
	GraphicsWindowFeatureImplementation<GraphicsWindowOpenGLContextFeature>(), PreInitializableClosable {
	final override val expresses: FeatureExpression<GraphicsWindowOpenGLContextFeature> =
		GraphicsWindowFeatures.OPENGL_CONTEXT
	var use: Boolean = false
}