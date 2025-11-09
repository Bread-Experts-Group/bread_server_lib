package org.bread_experts_group.api.graphics.feature.console.feature.device

import org.bread_experts_group.api.FeatureExpression

object GraphicsConsoleFeatures {
	val STANDARD_INPUT = object : FeatureExpression<GraphicsConsoleIOFeature> {
		override val name: String = "Standard Input"
	}

	val STANDARD_OUTPUT = object : FeatureExpression<GraphicsConsoleIOFeature> {
		override val name: String = "Standard Output"
	}

	val STANDARD_ERROR = object : FeatureExpression<GraphicsConsoleIOFeature> {
		override val name: String = "Standard Error"
	}
}