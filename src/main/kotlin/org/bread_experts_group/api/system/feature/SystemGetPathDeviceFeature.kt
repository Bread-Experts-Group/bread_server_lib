package org.bread_experts_group.api.system.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice

abstract class SystemGetPathDeviceFeature(
	override val source: ImplementationSource,
	override val expresses: FeatureExpression<SystemGetPathDeviceFeature>
) : SystemFeatureImplementation<SystemGetPathDeviceFeature>() {
	abstract val device: SystemDevice
}