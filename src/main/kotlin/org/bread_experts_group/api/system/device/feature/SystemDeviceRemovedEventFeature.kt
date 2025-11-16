package org.bread_experts_group.api.system.device.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.EventListener
import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.feature.SystemFeatureImplementation

abstract class SystemDeviceRemovedEventFeature : SystemFeatureImplementation<SystemDeviceRemovedEventFeature>() {
	override val expresses: FeatureExpression<SystemDeviceRemovedEventFeature> = SystemFeatures.DEVICE_REMOVED_EVENT
	abstract fun listen(with: (SystemDevice) -> Unit): EventListener
}