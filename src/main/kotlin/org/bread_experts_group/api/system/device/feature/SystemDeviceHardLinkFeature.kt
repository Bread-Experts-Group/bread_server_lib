package org.bread_experts_group.api.system.device.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceFeatureImplementation
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.hardlink.HardLinkSystemDeviceFeatureIdentifier

abstract class SystemDeviceHardLinkFeature : SystemDeviceFeatureImplementation<SystemDeviceHardLinkFeature>() {
	override val expresses: FeatureExpression<SystemDeviceHardLinkFeature> = SystemDeviceFeatures.HARD_LINK
	abstract fun link(
		towards: SystemDevice,
		vararg features: HardLinkSystemDeviceFeatureIdentifier
	): List<HardLinkSystemDeviceFeatureIdentifier>
}