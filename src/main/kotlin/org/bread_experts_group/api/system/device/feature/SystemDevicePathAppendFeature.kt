package org.bread_experts_group.api.system.device.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceFeatureImplementation
import org.bread_experts_group.api.system.device.SystemDeviceFeatures

abstract class SystemDevicePathAppendFeature :
	SystemDeviceFeatureImplementation<SystemDevicePathAppendFeature>() {
	override val expresses: FeatureExpression<SystemDevicePathAppendFeature> = SystemDeviceFeatures.PATH_APPEND
	abstract fun append(element: String): SystemDevice
}