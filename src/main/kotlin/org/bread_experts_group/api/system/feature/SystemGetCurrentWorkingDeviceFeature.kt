package org.bread_experts_group.api.system.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.device.SystemDevice

abstract class SystemGetCurrentWorkingDeviceFeature :
	SystemFeatureImplementation<SystemGetCurrentWorkingDeviceFeature>() {
	override val expresses: FeatureExpression<SystemGetCurrentWorkingDeviceFeature> =
		SystemFeatures.GET_CURRENT_WORKING_DEVICE
	abstract val device: SystemDevice
}