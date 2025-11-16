package org.bread_experts_group.api.graphics.feature.console.feature.device.feature

import org.bread_experts_group.api.coding.CodingFormat
import org.bread_experts_group.api.feature.FeatureExpression

abstract class GraphicsConsoleIOCodingSetFeature :
	GraphicsConsoleIOFeatureImplementation<GraphicsConsoleIOCodingSetFeature>() {
	override val expresses: FeatureExpression<GraphicsConsoleIOCodingSetFeature> = GraphicsConsoleIOFeatures.CODING_SET
	abstract fun setCoding(coding: CodingFormat)
}