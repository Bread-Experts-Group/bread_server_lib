package org.bread_experts_group.api.graphics

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.FeatureProvider
import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.graphics.feature.GraphicsFeatureImplementation
import org.bread_experts_group.logging.ColoredHandler
import java.util.*
import java.util.logging.Level

private typealias GFI = GraphicsFeatureImplementation<*>

object GraphicsProvider : FeatureProvider<GFI> {
	private val features = ServiceLoader.load(GFI::class.java).toMutableList()
	private val supportedFeatures = mutableMapOf<FeatureExpression<out GFI>, MutableList<GFI>>()
	private val logger = ColoredHandler.newLogger("TMP logger")
	override fun <I : GFI, E : FeatureExpression<I>> get(
		feature: E,
		allowEmulated: Boolean
	): I? {
		val supported = supportedFeatures[feature]
		if (supported != null) {
			@Suppress("UNCHECKED_CAST")
			return (if (allowEmulated) supported.firstOrNull()
			else supported.firstOrNull { it.source != ImplementationSource.JVM_EMULATED }) as? I
		}
		features.removeIf {
			try {
				!it.supported()
			} catch (e: Throwable) {
				logger.log(Level.FINE, e) { "Exception during graphics feature support check" }
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
}