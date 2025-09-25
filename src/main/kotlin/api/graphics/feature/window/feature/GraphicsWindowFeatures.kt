package org.bread_experts_group.api.graphics.feature.window.feature

import org.bread_experts_group.api.FeatureExpression

object GraphicsWindowFeatures {
	val WINDOW_NAME = object : FeatureExpression<GraphicsWindowNameFeature> {}
	val RENDER_LAMBDA = object : FeatureExpression<GraphicsWindowRenderEventFeature> {}
	val RESIZE_LAMBDA = object : FeatureExpression<GraphicsWindowResizeEventFeature> {}
	val OPENGL_CONTEXT = object : FeatureExpression<GraphicsWindowOpenGLContextFeature> {}
	val DIRECTX_CONTEXT = object : FeatureExpression<GraphicsWindowDirectXContext> {}
}