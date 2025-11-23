package org.bread_experts_group.api.system.device.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDeviceFeatureImplementation

class SystemDeviceBasicIdentifierFeature(
	override val source: ImplementationSource,
	override val expresses: FeatureExpression<SystemDeviceBasicIdentifierFeature>,
	val identity: Any
) : SystemDeviceFeatureImplementation<SystemDeviceBasicIdentifierFeature>() {
	override fun supported(): Boolean = true
}