package org.bread_experts_group.api.graphics.feature.console.feature.device.feature

import org.bread_experts_group.api.feature.FeatureExpression
import java.util.*

abstract class GraphicsConsoleIOModeGetFeature :
	GraphicsConsoleIOFeatureImplementation<GraphicsConsoleIOModeGetFeature>() {
	override val expresses: FeatureExpression<GraphicsConsoleIOModeGetFeature> = GraphicsConsoleIOFeatures.MODE_GET
	abstract val mode: EnumSet<GraphicsConsoleModes>
}