package org.bread_experts_group.api.system.device.feature

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDeviceFeatureImplementation
import org.bread_experts_group.api.system.device.SystemDeviceFeatures

class SystemDeviceFriendlyNameFeature(
	val name: String,
	override val source: ImplementationSource
) : SystemDeviceFeatureImplementation<SystemDeviceFriendlyNameFeature>() {
	override val expresses: FeatureExpression<SystemDeviceFriendlyNameFeature> =
		SystemDeviceFeatures.FRIENDLY_NAME

	override fun supported(): Boolean = true
}