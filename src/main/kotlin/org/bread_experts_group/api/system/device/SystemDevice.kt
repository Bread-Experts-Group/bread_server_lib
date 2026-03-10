package org.bread_experts_group.api.system.device

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.system.device.type.SystemDeviceTypeIdentifier

class SystemDevice(
	val type: SystemDeviceTypeIdentifier
) : FeatureProvider<SystemDeviceFeatureImplementation<*>> {
	override val logger
		get() = TODO("REPLACE LOGGER")
	override val supportedFeatures: MutableMap<
			FeatureExpression<out SystemDeviceFeatureImplementation<*>>,
			MutableList<SystemDeviceFeatureImplementation<*>>> = mutableMapOf()
	override val features: MutableList<SystemDeviceFeatureImplementation<*>> = mutableListOf()
}