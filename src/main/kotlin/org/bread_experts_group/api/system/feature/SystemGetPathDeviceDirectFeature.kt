package org.bread_experts_group.api.system.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice

abstract class SystemGetPathDeviceDirectFeature(
	override val source: ImplementationSource,
	override val expresses: FeatureExpression<SystemGetPathDeviceDirectFeature>
) : SystemFeatureImplementation<SystemGetPathDeviceDirectFeature>() {
	abstract fun get(device: String): SystemDevice
}