package org.bread_experts_group.api.coding

import org.bread_experts_group.api.coding.feature.jvm.JVMPortableNetworkGraphicsCodingFeature
import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.logging.ColoredHandler
import java.util.logging.Logger

object CodingFormatsProvider : FeatureProvider<CodingFeatureImplementation<*>> {
	override val logger: Logger = ColoredHandler.newLogger("TMP logger")
	override val features: MutableList<CodingFeatureImplementation<*>> = mutableListOf(
		JVMPortableNetworkGraphicsCodingFeature()
	)
	override val supportedFeatures: MutableMap<
			FeatureExpression<out CodingFeatureImplementation<*>>,
			MutableList<CodingFeatureImplementation<*>>> = mutableMapOf()
}