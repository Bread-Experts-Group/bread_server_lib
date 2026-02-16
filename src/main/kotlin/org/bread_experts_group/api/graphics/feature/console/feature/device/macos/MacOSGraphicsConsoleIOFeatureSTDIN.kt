package org.bread_experts_group.api.graphics.feature.console.feature.device.macos

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.graphics.feature.console.feature.device.GraphicsConsoleFeatures
import org.bread_experts_group.api.graphics.feature.console.feature.device.GraphicsConsoleIOFeature

class MacOSGraphicsConsoleIOFeatureSTDIN : GraphicsConsoleIOFeature() {
	override val expresses: FeatureExpression<GraphicsConsoleIOFeature> = GraphicsConsoleFeatures.STANDARD_INPUT
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE

	override fun supported(): Boolean = true
}