package org.bread_experts_group.api.system.device.io.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.io.IODevice
import org.bread_experts_group.api.system.device.io.IODeviceFeatureImplementation
import org.bread_experts_group.api.system.device.io.IODeviceFeatures
import org.bread_experts_group.api.system.device.io.open.ReOpenIODeviceFeatureIdentifier

abstract class IODeviceReopenFeature : IODeviceFeatureImplementation<IODeviceReopenFeature>() {
	override val expresses: FeatureExpression<IODeviceReopenFeature> = IODeviceFeatures.REOPEN
	abstract fun reopen(
		vararg features: ReOpenIODeviceFeatureIdentifier
	): Pair<IODevice, List<ReOpenIODeviceFeatureIdentifier>>?
}