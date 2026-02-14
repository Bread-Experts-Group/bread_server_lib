package org.bread_experts_group.api.system.device.feature.copy

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.system.device.copy.CopySystemDeviceFeatureIdentifier

abstract class SystemDeviceCopyHandle : FeatureProvider<SystemDeviceCopyFeatureImplementation<*>> {
	override val logger
		get() = TODO("not implemented")
	override val supportedFeatures: MutableMap<
			FeatureExpression<out SystemDeviceCopyFeatureImplementation<*>>,
			MutableList<SystemDeviceCopyFeatureImplementation<*>>> = mutableMapOf()

	abstract fun start(
		vararg features: CopySystemDeviceFeatureIdentifier
	): List<CopySystemDeviceFeatureIdentifier>
}