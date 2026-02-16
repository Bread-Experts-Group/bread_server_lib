package org.bread_experts_group.api.graphics.feature.window.feature

import org.bread_experts_group.api.feature.FeatureExpression

object GraphicsWindowFeatures {
	val WINDOW_NAME = object : FeatureExpression<GraphicsWindowNameFeature> {
		override val name: String = "Modify / Read Window Title"
	}

	val WINDOW_STATUS = object : FeatureExpression<GraphicsWindowStatusFeature> {
		override val name: String = "Modify / Read Window Status"
	}

	val WINDOW_DISPLAY_AFFINITY = object : FeatureExpression<GraphicsWindowDisplayAffinityFeature> {
		override val name: String = "Modify / Read Window Display Affinity"
	}

	val WINDOW_EVENT = object : FeatureExpression<GraphicsWindowEventLoopFeature> {
		override val name: String = "Control Window Event / Message Loop"
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