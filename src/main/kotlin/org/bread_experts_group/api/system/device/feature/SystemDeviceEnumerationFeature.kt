package org.bread_experts_group.api.system.device.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.device.enumerate.SystemDeviceEnumerationDataIdentifier
import org.bread_experts_group.api.system.device.enumerate.SystemDeviceEnumerationFeatureIdentifier
import org.bread_experts_group.api.system.feature.SystemFeatureImplementation

abstract class SystemDeviceEnumerationFeature : SystemFeatureImplementation<SystemDeviceEnumerationFeature>() {
	override val expresses: FeatureExpression<SystemDeviceEnumerationFeature> = SystemFeatures.ENUMERATE_DEVICES
	abstract fun enumerate(
		vararg features: SystemDeviceEnumerationFeatureIdentifier
	): List<SystemDeviceEnumerationDataIdentifier>
}