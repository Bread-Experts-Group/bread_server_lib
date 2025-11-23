package org.bread_experts_group.api.system.device.io.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.io.IODeviceFeatureImplementation
import org.bread_experts_group.api.system.device.io.IODeviceFeatures
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

abstract class IODeviceReadFeature : IODeviceFeatureImplementation<IODeviceReadFeature>() {
	override val expresses: FeatureExpression<IODeviceReadFeature> = IODeviceFeatures.READ

	abstract fun read(into: MemorySegment): Int
	fun read(into: ByteArray, offset: Int = 0, length: Int = into.size): Int = Arena.ofConfined().use {
		val allocated = it.allocate(length.toLong() - offset)
		val read = read(allocated)
		MemorySegment.copy(
			allocated, ValueLayout.JAVA_BYTE, 0,
			into, offset,
			read
		)
		read
	}
}