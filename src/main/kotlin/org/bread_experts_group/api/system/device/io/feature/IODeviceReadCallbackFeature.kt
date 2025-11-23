package org.bread_experts_group.api.system.device.io.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.io.IODeviceFeatureImplementation
import org.bread_experts_group.api.system.device.io.IODeviceFeatures
import java.lang.foreign.MemorySegment

abstract class IODeviceReadCallbackFeature : IODeviceFeatureImplementation<IODeviceReadCallbackFeature>() {
	override val expresses: FeatureExpression<IODeviceReadCallbackFeature> = IODeviceFeatures.READ_CALLBACK
	abstract fun read(into: (MemorySegment) -> Unit)
}