package org.bread_experts_group.api.system.io.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.io.IODeviceFeatureImplementation
import org.bread_experts_group.api.system.io.IODeviceFeatures
import java.lang.AutoCloseable
import java.lang.ref.Cleaner

open class IODeviceReleaseFeature(
	override val source: ImplementationSource,
	private val release: Cleaner.Cleanable
) : IODeviceFeatureImplementation<IODeviceReleaseFeature>(), AutoCloseable {
	override val expresses: FeatureExpression<IODeviceReleaseFeature> = IODeviceFeatures.RELEASE
	override fun supported(): Boolean = true
	override fun close() = release.clean()
}