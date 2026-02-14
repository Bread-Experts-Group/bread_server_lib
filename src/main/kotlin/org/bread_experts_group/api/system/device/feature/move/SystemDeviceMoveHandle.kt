package org.bread_experts_group.api.system.device.feature.move

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.system.device.move.MoveSystemDeviceFeatureIdentifier

abstract class SystemDeviceMoveHandle : FeatureProvider<SystemDeviceMoveFeatureImplementation<*>> {
	override val logger
		get() = TODO("not implemented")
	override val supportedFeatures: MutableMap<
			FeatureExpression<out SystemDeviceMoveFeatureImplementation<*>>,
			MutableList<SystemDeviceMoveFeatureImplementation<*>>> = mutableMapOf()

	abstract fun start(
		vararg features: MoveSystemDeviceFeatureIdentifier
	): List<MoveSystemDeviceFeatureIdentifier>
}