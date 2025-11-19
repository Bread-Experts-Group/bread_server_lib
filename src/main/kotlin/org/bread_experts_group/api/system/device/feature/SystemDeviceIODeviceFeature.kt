package org.bread_experts_group.api.system.device.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.SystemDeviceFeatureImplementation
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.io.IODevice
import org.bread_experts_group.api.system.device.io.OpenIODeviceFeatureIdentifier

abstract class SystemDeviceIODeviceFeature : SystemDeviceFeatureImplementation<SystemDeviceIODeviceFeature>() {
	override val expresses: FeatureExpression<SystemDeviceIODeviceFeature> = SystemDeviceFeatures.IO_DEVICE
	abstract fun open(
		vararg features: OpenIODeviceFeatureIdentifier
	): Pair<IODevice, List<OpenIODeviceFeatureIdentifier>>?
}