package org.bread_experts_group.api.system.device.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceFeatureImplementation
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.softlink.SoftLinkSystemDeviceFeatureIdentifier

abstract class SystemDeviceSoftLinkFeature : SystemDeviceFeatureImplementation<SystemDeviceSoftLinkFeature>() {
	override val expresses: FeatureExpression<SystemDeviceSoftLinkFeature> = SystemDeviceFeatures.PATH_SOFT_LINK
	abstract fun link(
		towards: SystemDevice,
		vararg features: SoftLinkSystemDeviceFeatureIdentifier
	): List<SoftLinkSystemDeviceFeatureIdentifier>
}