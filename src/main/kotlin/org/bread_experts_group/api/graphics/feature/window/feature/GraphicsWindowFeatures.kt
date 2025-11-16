package org.bread_experts_group.api.graphics.feature.window.feature

import org.bread_experts_group.api.feature.FeatureExpression

object GraphicsWindowFeatures {
	val WINDOW_NAME = object : FeatureExpression<GraphicsWindowNameFeature> {
		override val name: String = "Modify / Read Window Title"
	}

	val RENDER_LAMBDA = object : FeatureExpression<GraphicsWindowRenderEventFeature> {
		override val name: String = "Lambda on Render / Re-paint"
	}

	val RESIZE_LAMBDA = object : FeatureExpression<GraphicsWindowResizeEventFeature> {
		override val name: String = "Lambda on Resize"
	}

	val OPENGL_CONTEXT = object : FeatureExpression<GraphicsWindowOpenGLContextFeature> {
		override val name: String = "OpenGL Window Context"
	}

	val DIRECTX_CONTEXT = object : FeatureExpression<GraphicsWindowDirectXContext> {
		override val name: String = "DirectX Window Context"
	}
}