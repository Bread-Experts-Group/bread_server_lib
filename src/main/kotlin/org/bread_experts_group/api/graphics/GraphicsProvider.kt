package org.bread_experts_group.api.graphics

import org.bread_experts_group.api.apiRootLogger
import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.graphics.feature.console.macos.MacOSGraphicsConsoleFeature
import org.bread_experts_group.api.graphics.feature.console.windows.WindowsGraphicsConsoleFeature
import org.bread_experts_group.api.graphics.feature.direct2d.windows.WindowsGraphicsWindowDirect2DFeature
import org.bread_experts_group.api.graphics.feature.directwrite.windows.WindowsGraphicsWindowDirectWriteFeature
import org.bread_experts_group.api.graphics.feature.window.windows.WindowsGraphicsWindowFeature
import org.bread_experts_group.generic.logging.LevelLogger

object GraphicsProvider : FeatureProvider<GraphicsFeatureImplementation<*>> {
	override val logger = LevelLogger("graphics", apiRootLogger)
	override val features: MutableList<GraphicsFeatureImplementation<*>> = mutableListOf(
		WindowsGraphicsWindowFeature(),
		WindowsGraphicsConsoleFeature(),
		WindowsGraphicsWindowDirect2DFeature(),
		WindowsGraphicsWindowDirectWriteFeature(),
		MacOSGraphicsConsoleFeature()
	)
	override val supportedFeatures: MutableMap<
			FeatureExpression<out GraphicsFeatureImplementation<*>>,
			MutableList<GraphicsFeatureImplementation<*>>
			> = mutableMapOf()
}