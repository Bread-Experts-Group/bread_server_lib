package org.bread_experts_group.api.io.feature.device

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.FeatureProvider
import org.bread_experts_group.logging.ColoredHandler
import java.util.logging.Logger

abstract class IODevice : FeatureProvider<IODeviceFeatureImplementation<*>> {
	override val logger: Logger = ColoredHandler.newLogger("TMP logger")
	override val supportedFeatures: MutableMap<
			FeatureExpression<out IODeviceFeatureImplementation<*>>,
			MutableList<IODeviceFeatureImplementation<*>>> = mutableMapOf()
}