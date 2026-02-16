package org.bread_experts_group.api.graphics.feature.console.feature.device.macos

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.graphics.feature.console.feature.device.GraphicsConsoleIOFeature

abstract class MacOSGraphicsConsoleIOFeature(
	override val expresses: FeatureExpression<GraphicsConsoleIOFeature>,
	private val handle: UInt
) : GraphicsConsoleIOFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE

	override fun supported(): Boolean {
		//if (!arrayOf(0u, 1u, 2u).contains(handle)) return false
		// features.add(MacOSIODevice(handle))
		return arrayOf(0u, 1u, 2u).contains(handle)
	}
}