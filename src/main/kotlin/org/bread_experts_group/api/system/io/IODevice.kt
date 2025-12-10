package org.bread_experts_group.api.system.io

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.system.io.open.OpenIODeviceDataIdentifier
import org.bread_experts_group.logging.ColoredHandler
import java.lang.ref.Cleaner
import java.util.logging.Logger

abstract class IODevice : FeatureProvider<IODeviceFeatureImplementation<*>>, OpenIODeviceDataIdentifier {
	override val logger: Logger = ColoredHandler.newLogger("TMP logger")
	override val supportedFeatures: MutableMap<
			FeatureExpression<out IODeviceFeatureImplementation<*>>,
			MutableList<IODeviceFeatureImplementation<*>>> = mutableMapOf()

	companion object {
		private val cleaner = Cleaner.create()
	}

	internal fun registerCleaningAction(action: () -> Unit) = cleaner.register(this) { action() }
}