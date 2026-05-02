package org.bread_experts_group.api.graphics.feature.console

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.graphics.GraphicsFeatureImplementation
import org.bread_experts_group.api.graphics.GraphicsFeatures
import org.bread_experts_group.api.graphics.GraphicsProvider
import org.bread_experts_group.api.graphics.feature.console.feature.device.GraphicsConsoleFeatureImplementation
import org.bread_experts_group.api.graphics.feature.console.feature.device.macos.MacOSGraphicsConsoleIOFeatureSTDERR
import org.bread_experts_group.api.graphics.feature.console.feature.device.macos.MacOSGraphicsConsoleIOFeatureSTDIN
import org.bread_experts_group.api.graphics.feature.console.feature.device.macos.MacOSGraphicsConsoleIOFeatureSTDOUT
import org.bread_experts_group.api.graphics.feature.console.feature.device.windows.WindowsGraphicsConsoleIOFeatureSTDERR
import org.bread_experts_group.api.graphics.feature.console.feature.device.windows.WindowsGraphicsConsoleIOFeatureSTDIN
import org.bread_experts_group.api.graphics.feature.console.feature.device.windows.WindowsGraphicsConsoleIOFeatureSTDOUT
import org.bread_experts_group.generic.logging.LevelLogger

abstract class GraphicsConsoleFeature : GraphicsFeatureImplementation<GraphicsConsoleFeature>(),
	FeatureProvider<GraphicsConsoleFeatureImplementation<*>> {
	override val expresses: FeatureExpression<GraphicsConsoleFeature> = GraphicsFeatures.CUI_CONSOLE
	override val logger = LevelLogger("console", GraphicsProvider.logger)
	override val features: MutableList<GraphicsConsoleFeatureImplementation<*>> = mutableListOf(
		WindowsGraphicsConsoleIOFeatureSTDIN(),
		WindowsGraphicsConsoleIOFeatureSTDOUT(),
		WindowsGraphicsConsoleIOFeatureSTDERR(),
		MacOSGraphicsConsoleIOFeatureSTDIN(),
		MacOSGraphicsConsoleIOFeatureSTDOUT(),
		MacOSGraphicsConsoleIOFeatureSTDERR()
	)
	override val supportedFeatures: MutableMap<
			FeatureExpression<out GraphicsConsoleFeatureImplementation<*>>,
			MutableList<GraphicsConsoleFeatureImplementation<*>>> = mutableMapOf()
}