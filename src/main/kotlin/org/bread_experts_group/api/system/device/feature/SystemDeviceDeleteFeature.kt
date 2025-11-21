package org.bread_experts_group.api.system.device.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.SystemDeviceFeatureImplementation
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.delete.DeleteSystemDeviceFeatureIdentifier

abstract class SystemDeviceDeleteFeature : SystemDeviceFeatureImplementation<SystemDeviceDeleteFeature>() {
	override val expresses: FeatureExpression<SystemDeviceDeleteFeature> = SystemDeviceFeatures.DELETE
	abstract fun delete(
		vararg features: DeleteSystemDeviceFeatureIdentifier
	): List<DeleteSystemDeviceFeatureIdentifier>
}