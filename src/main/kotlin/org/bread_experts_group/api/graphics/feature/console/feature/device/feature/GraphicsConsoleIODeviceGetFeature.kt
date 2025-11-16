package org.bread_experts_group.api.graphics.feature.console.feature.device.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.io.IODevice

abstract class GraphicsConsoleIODeviceGetFeature :
	GraphicsConsoleIOFeatureImplementation<GraphicsConsoleIODeviceGetFeature>() {
	override val expresses: FeatureExpression<GraphicsConsoleIODeviceGetFeature> = GraphicsConsoleIOFeatures.DEVICE_GET
	abstract val device: IODevice
}