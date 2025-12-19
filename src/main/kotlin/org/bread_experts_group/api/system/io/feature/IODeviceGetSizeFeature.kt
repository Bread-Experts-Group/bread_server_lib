package org.bread_experts_group.api.system.io.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.io.IODeviceFeatureImplementation
import org.bread_experts_group.api.system.io.IODeviceFeatures
import org.bread_experts_group.api.system.io.size.GetSizeIODeviceDataIdentifier
import org.bread_experts_group.api.system.io.size.GetSizeIODeviceFeatureIdentifier

abstract class IODeviceGetSizeFeature : IODeviceFeatureImplementation<IODeviceGetSizeFeature>() {
	override val expresses: FeatureExpression<IODeviceGetSizeFeature> = IODeviceFeatures.GET_SIZE

	abstract fun get(
		vararg features: GetSizeIODeviceFeatureIdentifier
	): List<GetSizeIODeviceDataIdentifier>
}