package org.bread_experts_group.api.system.io.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.io.IODeviceFeatureImplementation
import org.bread_experts_group.api.system.io.IODeviceFeatures
import org.bread_experts_group.api.system.io.flush.FlushIODeviceFeatureIdentifier

abstract class IODeviceFlushFeature : IODeviceFeatureImplementation<IODeviceFlushFeature>() {
	override val expresses: FeatureExpression<IODeviceFlushFeature> = IODeviceFeatures.FLUSH

	abstract fun flush(
		vararg features: FlushIODeviceFeatureIdentifier
	): List<FlushIODeviceFeatureIdentifier>
}