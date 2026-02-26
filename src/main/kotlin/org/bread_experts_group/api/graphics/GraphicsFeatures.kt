package org.bread_experts_group.api.graphics

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.graphics.feature.console.GraphicsConsoleFeature
import org.bread_experts_group.api.graphics.feature.direct2d.GraphicsWindowDirect2DFeature
import org.bread_experts_group.api.graphics.feature.directwrite.GraphicsWindowDirectWriteFeature
import org.bread_experts_group.api.graphics.feature.window.GraphicsWindowFeature

object GraphicsFeatures {
	val GUI_WINDOW = object : FeatureExpression<GraphicsWindowFeature> {
		override val name: String = "GUI Windowing"
	}

	val CUI_CONSOLE = object : FeatureExpression<GraphicsConsoleFeature> {
		override val name: String = "CUI Console"
	}

	val DIRECT2D = object : FeatureExpression<GraphicsWindowDirect2DFeature> {
		override val name: String = "Direct2D Object Management"
	}

	val DIRECTWRITE = object : FeatureExpression<GraphicsWindowDirectWriteFeature> {
		override val name: String = "DirectWrite Object Management"
	}
}