package org.bread_experts_group.api

import org.bread_experts_group.logging.ColoredHandler
import java.util.*
import java.util.logging.Level

abstract class FeatureProvisioner<X>(
	clazz: Class<X>,
	vararg features: X
) where X : FeatureImplementation<out X>, X : CheckedImplementation {
	protected val features = (ServiceLoader.load(clazz) + features).toMutableList()
	private val supportedFeatures = mutableMapOf<FeatureExpression<out X>, MutableList<X>>()
	protected val logger = ColoredHandler.newLogger("TMP logger")
	fun <I : X, E : FeatureExpression<I>> get(feature: E, allowEmulated: Boolean): I {
		val supported = supportedFeatures[feature]
		if (supported != null) {
			@Suppress("UNCHECKED_CAST")
			return (if (allowEmulated) supported.firstOrNull()
			else supported.firstOrNull { it.source != ImplementationSource.JVM_EMULATED }) as I
		}
		features.removeIf {
			try {
				!it.supported()
			} catch (e: Exception) {
				logger.log(Level.FINE, e) { "Exception during feature support check" }
				true
			}
		}
		val found = features.firstOrNull {
			it.expresses == feature &&
					(if (allowEmulated) true else it.source != ImplementationSource.JVM_EMULATED)
		} ?: throw NoFeatureAvailableException(feature.name)
		supportedFeatures.getOrPut(feature) { mutableListOf() }.add(found)
		@Suppress("UNCHECKED_CAST")
		return found as I
	}
}