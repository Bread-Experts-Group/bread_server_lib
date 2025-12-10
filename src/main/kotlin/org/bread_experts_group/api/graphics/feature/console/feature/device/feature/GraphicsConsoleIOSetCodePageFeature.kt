package org.bread_experts_group.api.graphics.feature.console.feature.device.feature

import org.bread_experts_group.api.coding.codepage.CodePageDataIdentifier
import org.bread_experts_group.api.feature.FeatureExpression

abstract class GraphicsConsoleIOSetCodePageFeature :
	GraphicsConsoleIOFeatureImplementation<GraphicsConsoleIOSetCodePageFeature>() {
	override val expresses: FeatureExpression<GraphicsConsoleIOSetCodePageFeature> =
		GraphicsConsoleIOFeatures.SET_CODE_PAGE

	abstract fun setCodePage(codePage: CodePageDataIdentifier)
}