package org.bread_experts_group.api.graphics.feature.console.feature.device.feature

import org.bread_experts_group.api.coding.codepage.CodePageDataIdentifier
import org.bread_experts_group.api.feature.FeatureExpression

abstract class GraphicsConsoleIOGetCodePageFeature :
	GraphicsConsoleIOFeatureImplementation<GraphicsConsoleIOGetCodePageFeature>() {
	override val expresses: FeatureExpression<GraphicsConsoleIOGetCodePageFeature> =
		GraphicsConsoleIOFeatures.GET_CODE_PAGE
	abstract val codePage: CodePageDataIdentifier
}