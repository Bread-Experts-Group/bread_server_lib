package org.bread_experts_group.api.graphics.feature.console.feature.device.feature

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.coding.CodingFormat

abstract class GraphicsConsoleIOCodingGetFeature :
	GraphicsConsoleIOFeatureImplementation<GraphicsConsoleIOCodingGetFeature>() {
	override val expresses: FeatureExpression<GraphicsConsoleIOCodingGetFeature> = GraphicsConsoleIOFeatures.CODING_GET
	abstract val coding: CodingFormat
}