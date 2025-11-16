package org.bread_experts_group.api.coding

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.logging.ColoredHandler
import java.util.*
import java.util.logging.Logger

object CodingFormatsProvider : FeatureProvider<CodingFormatImplementation> {
	override val logger: Logger = ColoredHandler.newLogger("TMP logger")
	override val features: MutableList<CodingFormatImplementation> = ServiceLoader.load(
		CodingFormatImplementation::class.java
	).toMutableList()
	override val supportedFeatures: MutableMap<
			FeatureExpression<out CodingFormatImplementation>,
			MutableList<CodingFormatImplementation>> = mutableMapOf()
}