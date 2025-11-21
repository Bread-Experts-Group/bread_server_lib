package org.bread_experts_group.api.system.device.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceFeatureImplementation
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.replace.SystemDeviceReplaceFeatureIdentifier

abstract class SystemDeviceReplaceFeature : SystemDeviceFeatureImplementation<SystemDeviceReplaceFeature>() {
	override val expresses: FeatureExpression<SystemDeviceReplaceFeature> = SystemDeviceFeatures.REPLACE
	abstract fun replace(
		with: SystemDevice,
		backup: SystemDevice?,
		vararg features: SystemDeviceReplaceFeatureIdentifier
	): List<SystemDeviceReplaceFeatureIdentifier>
}