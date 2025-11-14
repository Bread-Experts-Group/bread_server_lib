package org.bread_experts_group.api.system.device.feature

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceType
import org.bread_experts_group.api.system.feature.SystemFeatureImplementation

abstract class SystemDeviceEnumerationFeature : SystemFeatureImplementation<SystemDeviceEnumerationFeature>() {
	override val expresses: FeatureExpression<SystemDeviceEnumerationFeature> = SystemFeatures.ENUMERATE_DEVICES
	abstract fun enumerate(type: SystemDeviceType?): Iterable<SystemDevice>
}