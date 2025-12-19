package org.bread_experts_group.api.system.device.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDeviceFeatureImplementation
import org.bread_experts_group.api.system.device.metadata.MetadataSystemDeviceDataIdentifier
import org.bread_experts_group.api.system.device.metadata.MetadataSystemDeviceFeatureIdentifier

abstract class SystemDeviceGetTimeFeature(
	override val source: ImplementationSource,
	override val expresses: FeatureExpression<SystemDeviceGetTimeFeature>
) : SystemDeviceFeatureImplementation<SystemDeviceGetTimeFeature>() {
	abstract fun getTime(
		vararg features: MetadataSystemDeviceFeatureIdentifier
	): List<MetadataSystemDeviceDataIdentifier>
}