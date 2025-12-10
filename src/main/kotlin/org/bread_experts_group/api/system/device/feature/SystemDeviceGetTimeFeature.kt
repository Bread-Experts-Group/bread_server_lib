package org.bread_experts_group.api.system.device.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDeviceFeatureImplementation
import org.bread_experts_group.api.system.io.open.OpenIODeviceFeatureIdentifier
import java.time.Instant

abstract class SystemDeviceGetTimeFeature(
	override val source: ImplementationSource,
	override val expresses: FeatureExpression<SystemDeviceGetTimeFeature>
) : SystemDeviceFeatureImplementation<SystemDeviceGetTimeFeature>() {
	abstract fun getTime(
		vararg features: OpenIODeviceFeatureIdentifier
	): Pair<Instant, List<OpenIODeviceFeatureIdentifier>>
}