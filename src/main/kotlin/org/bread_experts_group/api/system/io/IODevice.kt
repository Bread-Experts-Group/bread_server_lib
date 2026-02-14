package org.bread_experts_group.api.system.io

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.api.system.io.open.OpenIODeviceDataIdentifier
import org.bread_experts_group.generic.logging.LevelLogger

class IODevice : FeatureProvider<IODeviceFeatureImplementation<*>>, OpenIODeviceDataIdentifier {
	override val logger = LevelLogger("io device", SystemProvider.logger)
	override val supportedFeatures: MutableMap<
			FeatureExpression<out IODeviceFeatureImplementation<*>>,
			MutableList<IODeviceFeatureImplementation<*>>> = mutableMapOf()
	override val features: MutableList<IODeviceFeatureImplementation<*>> = mutableListOf()
}