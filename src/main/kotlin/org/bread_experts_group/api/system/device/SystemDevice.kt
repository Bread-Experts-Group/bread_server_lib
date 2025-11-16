package org.bread_experts_group.api.system.device

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.logging.ColoredHandler
import java.util.logging.Logger

class SystemDevice(
	val type: SystemDeviceType
) : FeatureProvider<SystemDeviceFeatureImplementation<*>> {
	override val logger: Logger = ColoredHandler.newLogger("TMP logger")
	override val supportedFeatures: MutableMap<
			FeatureExpression<out SystemDeviceFeatureImplementation<*>>,
			MutableList<SystemDeviceFeatureImplementation<*>>> = mutableMapOf()
	override val features: MutableList<SystemDeviceFeatureImplementation<*>> = mutableListOf()
}