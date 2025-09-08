package org.bread_experts_group.api.graphics

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.FeatureImplementationSource
import org.bread_experts_group.api.FeatureProvider
import org.bread_experts_group.api.graphics.feature.GraphicsFeatureImplementation
import java.util.*

private typealias GFI = GraphicsFeatureImplementation<*>

object GraphicsProvider : FeatureProvider<GFI> {
	private val features = ServiceLoader.load(GFI::class.java).toMutableList()
	private val supportedFeatures = mutableMapOf<FeatureExpression<out GFI>, MutableList<GFI>>()
	override fun <I : GFI, E : FeatureExpression<I>> get(
		feature: E,
		allowEmulated: Boolean
	): I? {
		val supported = supportedFeatures[feature]
		if (supported != null) {
			@Suppress("UNCHECKED_CAST")
			return (if (allowEmulated) supported.firstOrNull()
			else supported.firstOrNull { it.source != FeatureImplementationSource.JVM_EMULATED }) as? I
		}
		features.removeIf {
			try {
				!it.supported()
			} catch (_: Throwable) {
				true
			}
		}
		val found = features.firstOrNull {
			it.expresses == feature &&
					(if (allowEmulated) true else it.source != FeatureImplementationSource.JVM_EMULATED)
		} ?: return null
		supportedFeatures.getOrPut(feature) { mutableListOf() }.add(found)
		@Suppress("UNCHECKED_CAST")
		return found as I
	}
}