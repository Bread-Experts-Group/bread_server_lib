package org.bread_experts_group.api.system.device

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import java.lang.ref.Cleaner
import java.util.logging.Logger

class SystemDevice(
	val type: SystemDeviceType
) : FeatureProvider<SystemDeviceFeatureImplementation<*>> {
	override val logger: Logger
		get() = TODO("REPLACE LOGGER")
	override val supportedFeatures: MutableMap<
			FeatureExpression<out SystemDeviceFeatureImplementation<*>>,
			MutableList<SystemDeviceFeatureImplementation<*>>> = mutableMapOf()
	override val features: MutableList<SystemDeviceFeatureImplementation<*>> = mutableListOf()

	companion object {
		private val cleaner = Cleaner.create()
	}

	internal fun registerCleaningAction(action: () -> Unit) = cleaner.register(this) { action() }
}