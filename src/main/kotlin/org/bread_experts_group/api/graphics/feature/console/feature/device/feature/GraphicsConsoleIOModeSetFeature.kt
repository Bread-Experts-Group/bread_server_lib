package org.bread_experts_group.api.graphics.feature.console.feature.device.feature

import org.bread_experts_group.api.feature.FeatureExpression
import java.util.*

abstract class GraphicsConsoleIOModeSetFeature :
	GraphicsConsoleIOFeatureImplementation<GraphicsConsoleIOModeSetFeature>() {
	override val expresses: FeatureExpression<GraphicsConsoleIOModeSetFeature> = GraphicsConsoleIOFeatures.MODE_SET
	abstract fun setMode(set: EnumSet<GraphicsConsoleModes>)
}