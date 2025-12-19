package org.bread_experts_group.api.system.io.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.io.IODeviceFeatureImplementation
import org.bread_experts_group.api.system.io.IODeviceFeatures
import org.bread_experts_group.api.system.io.size.SetSizeIODeviceDataIdentifier
import org.bread_experts_group.api.system.io.size.SetSizeIODeviceFeatureIdentifier

abstract class IODeviceSetSizeFeature : IODeviceFeatureImplementation<IODeviceSetSizeFeature>() {
	override val expresses: FeatureExpression<IODeviceSetSizeFeature> = IODeviceFeatures.SET_SIZE

	abstract fun set(
		vararg features: SetSizeIODeviceFeatureIdentifier
	): List<SetSizeIODeviceDataIdentifier>
}