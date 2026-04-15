package org.bread_experts_group.api.system.device.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDeviceFeatureImplementation

abstract class SystemDevicePathElementFeature(
	override val expresses: FeatureExpression<SystemDevicePathElementFeature>
) : SystemDeviceFeatureImplementation<SystemDevicePathElementFeature>() {
	abstract val element: String

	class Fixed(
		expresses: FeatureExpression<SystemDevicePathElementFeature>,
		override val source: ImplementationSource,
		override val element: String
	) : SystemDevicePathElementFeature(expresses) {
		override fun supported(): Boolean = true
	}
}