package org.bread_experts_group.api.system.device.io.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.io.IODeviceFeatureImplementation
import org.bread_experts_group.api.system.device.io.IODeviceFeatures
import org.bread_experts_group.api.system.device.io.seek.SeekIODeviceFeatureIdentifier

abstract class IODeviceSeekFeature : IODeviceFeatureImplementation<IODeviceSeekFeature>() {
	override val expresses: FeatureExpression<IODeviceSeekFeature> = IODeviceFeatures.SEEK

	abstract fun seek(
		n: Long,
		vararg features: SeekIODeviceFeatureIdentifier
	): Pair<Long, List<SeekIODeviceFeatureIdentifier>>
}