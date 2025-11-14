package org.bread_experts_group.api.system.device.io.feature

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.system.device.io.IODeviceFeatureImplementation
import org.bread_experts_group.api.system.device.io.IODeviceFeatures
import java.lang.foreign.MemorySegment

abstract class IODeviceReadFeature : IODeviceFeatureImplementation<IODeviceReadFeature>() {
	override val expresses: FeatureExpression<IODeviceReadFeature> = IODeviceFeatures.READ

	abstract fun read(into: MemorySegment, length: Int): Int
	abstract fun read(into: ByteArray, offset: Int = 0, length: Int = into.size): Int
}