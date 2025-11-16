package org.bread_experts_group.api.system.device.feature

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceFeatureImplementation
import org.bread_experts_group.api.system.device.SystemDeviceFeatures

abstract class SystemDeviceParentFeature : SystemDeviceFeatureImplementation<SystemDeviceParentFeature>() {
	override val expresses: FeatureExpression<SystemDeviceParentFeature> = SystemDeviceFeatures.PARENT
	abstract val parent: SystemDevice
}