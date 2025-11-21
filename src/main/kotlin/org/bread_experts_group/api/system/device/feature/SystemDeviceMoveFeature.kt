package org.bread_experts_group.api.system.device.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceFeatureImplementation
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.move.MoveSystemDeviceFeatureIdentifier

abstract class SystemDeviceMoveFeature : SystemDeviceFeatureImplementation<SystemDeviceMoveFeature>() {
	override val expresses: FeatureExpression<SystemDeviceMoveFeature> = SystemDeviceFeatures.MOVE
	abstract fun move(
		destination: SystemDevice,
		vararg features: MoveSystemDeviceFeatureIdentifier
	): Pair<SystemDeviceMoveHandle, List<MoveSystemDeviceFeatureIdentifier>>

	class SystemDeviceMoveHandle {
	}
}