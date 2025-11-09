package org.bread_experts_group.api.system.device.feature

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDeviceFeatureImplementation
import org.bread_experts_group.api.system.device.SystemDeviceFeatures

class SystemDeviceSystemIdentityFeature(
	val identity: String,
	override val source: ImplementationSource
) : SystemDeviceFeatureImplementation<SystemDeviceSystemIdentityFeature>() {
	override val expresses: FeatureExpression<SystemDeviceSystemIdentityFeature> =
		SystemDeviceFeatures.SYSTEM_IDENTIFIER

	override fun supported(): Boolean = true
}