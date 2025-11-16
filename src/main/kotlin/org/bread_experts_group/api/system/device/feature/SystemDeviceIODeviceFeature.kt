package org.bread_experts_group.api.system.device.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureIdentifier
import org.bread_experts_group.api.system.device.SystemDeviceFeatureImplementation
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.io.IODevice

abstract class SystemDeviceIODeviceFeature : SystemDeviceFeatureImplementation<SystemDeviceIODeviceFeature>() {
	override val expresses: FeatureExpression<SystemDeviceIODeviceFeature> = SystemDeviceFeatures.IO_DEVICE
	override fun supported(): Boolean = true
	abstract fun open(vararg features: FeatureIdentifier): Pair<IODevice, List<FeatureIdentifier>>
}