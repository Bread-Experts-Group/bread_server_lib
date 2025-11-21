package org.bread_experts_group.api.system.device.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceFeatureImplementation
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.copy.CopySystemDeviceFeatureIdentifier

abstract class SystemDeviceCopyFeature : SystemDeviceFeatureImplementation<SystemDeviceCopyFeature>() {
	override val expresses: FeatureExpression<SystemDeviceCopyFeature> = SystemDeviceFeatures.COPY
	abstract fun copy(
		destination: SystemDevice,
		vararg features: CopySystemDeviceFeatureIdentifier
	): Pair<SystemDeviceCopyHandle, List<CopySystemDeviceFeatureIdentifier>>

	class SystemDeviceCopyHandle {
	}
}