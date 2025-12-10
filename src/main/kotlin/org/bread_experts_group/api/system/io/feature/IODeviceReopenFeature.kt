package org.bread_experts_group.api.system.io.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.io.IODevice
import org.bread_experts_group.api.system.io.IODeviceFeatureImplementation
import org.bread_experts_group.api.system.io.IODeviceFeatures
import org.bread_experts_group.api.system.io.open.OpenIODeviceFeatureIdentifier
import org.bread_experts_group.api.system.io.open.ReOpenIODeviceFeatureIdentifier

abstract class IODeviceReopenFeature : IODeviceFeatureImplementation<IODeviceReopenFeature>() {
	override val expresses: FeatureExpression<IODeviceReopenFeature> = IODeviceFeatures.REOPEN
	abstract fun reopen(
		vararg features: ReOpenIODeviceFeatureIdentifier
	): Pair<IODevice, List<OpenIODeviceFeatureIdentifier>>
}