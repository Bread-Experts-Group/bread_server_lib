package org.bread_experts_group.api

import java.util.logging.Level
import java.util.logging.Logger

/**
 * Feature providers allow programs to use features from APIs where *expressed features* may not be present across all
 * systems (e.g. graphics), and still allow them to take advantage where they are present.
 * Additionally, providers allow the use of *emulated features,* where the expressed feature cannot be more effectively
 * produced natively, and is implemented in the JVM.
 * * *expressed feature:* A generic feature, such as the concept of windowing.
 * * *implemented feature:* A feature that can be produced on the local system, such as through a Desktop Environment.
 * * *emulated feature:* A feature that can be produced on the local system, using platform-agnostic features through
 * the JVM.
 * @see FeatureImplementation
 * @author Miko Elbrecht
 * @since D0F0N0P0
 */
interface FeatureProvider<X : FeatureImplementation<out X>> {
	val features: MutableList<X>
	val supportedFeatures: MutableMap<FeatureExpression<out X>, MutableList<X>>
	val logger: Logger

	fun <I : X, E : FeatureExpression<I>> getOrNull(feature: E, allowEmulated: Boolean = false): I? {
		val supported = supportedFeatures[feature]
		if (supported != null) {
			@Suppress("UNCHECKED_CAST")
			return (if (allowEmulated) supported.firstOrNull()
			else supported.firstOrNull { it.source != ImplementationSource.JVM_EMULATED }) as I
		}
		features.removeIf {
			try {
				if (it is CheckedImplementation) !it.supported()
				else false
			} catch (e: Exception) {
				logger.log(Level.INFO, e) { "Exception during feature support check" }
				true
			}
		}
		val found = features.firstOrNull {
			it.expresses == feature &&
					(if (allowEmulated) true else it.source != ImplementationSource.JVM_EMULATED)
		} ?: return null
		supportedFeatures.getOrPut(feature) { mutableListOf() }.add(found)
		@Suppress("UNCHECKED_CAST")
		return found as I
	}

	fun <I : X, E : FeatureExpression<I>> get(feature: E, allowEmulated: Boolean = false): I {
		return getOrNull(feature, allowEmulated) ?: throw NoFeatureAvailableException(feature.name)
	}
}