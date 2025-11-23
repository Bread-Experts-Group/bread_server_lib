package org.bread_experts_group.api.system.device.io.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.io.IODeviceFeatureImplementation
import org.bread_experts_group.api.system.device.io.IODeviceFeatures
import org.bread_experts_group.api.system.device.io.feature.lock.IODeviceDataRangeLockHandle
import org.bread_experts_group.api.system.device.io.lock.IODeviceDataRangeLockFeatureIdentifier

abstract class IODeviceDataRangeLockFeature : IODeviceFeatureImplementation<IODeviceDataRangeLockFeature>() {
	override val expresses: FeatureExpression<IODeviceDataRangeLockFeature> = IODeviceFeatures.DATA_RANGE_LOCK

	abstract fun acquire(
		length: Long,
		vararg features: IODeviceDataRangeLockFeatureIdentifier
	): Pair<IODeviceDataRangeLockHandle, List<IODeviceDataRangeLockFeatureIdentifier>>
}