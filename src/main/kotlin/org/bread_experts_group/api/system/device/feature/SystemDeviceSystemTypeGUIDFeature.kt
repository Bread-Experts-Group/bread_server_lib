package org.bread_experts_group.api.system.device.feature

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDeviceFeatureImplementation
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.ffi.GUID

class SystemDeviceSystemTypeGUIDFeature(
	val guid: GUID,
	override val source: ImplementationSource
) : SystemDeviceFeatureImplementation<SystemDeviceSystemTypeGUIDFeature>() {
	override val expresses: FeatureExpression<SystemDeviceSystemTypeGUIDFeature> =
		SystemDeviceFeatures.SYSTEM_TYPE_GUID

	override fun supported(): Boolean = true
}