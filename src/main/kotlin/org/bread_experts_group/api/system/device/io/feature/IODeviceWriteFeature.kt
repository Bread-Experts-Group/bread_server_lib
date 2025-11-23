package org.bread_experts_group.api.system.device.io.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.io.IODeviceFeatureImplementation
import org.bread_experts_group.api.system.device.io.IODeviceFeatures
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.nio.charset.Charset

abstract class IODeviceWriteFeature : IODeviceFeatureImplementation<IODeviceWriteFeature>() {
	override val expresses: FeatureExpression<IODeviceWriteFeature> = IODeviceFeatures.WRITE

	abstract fun write(from: MemorySegment): Int
	fun write(from: ByteArray, offset: Int = 0, length: Int = from.size): Int = Arena.ofConfined().use {
		val copied = it.allocate(length.toLong())
		MemorySegment.copy(
			from, offset,
			copied, ValueLayout.JAVA_BYTE, 0,
			length
		)
		write(copied)
	}

	fun write(text: String, coding: Charset): Int {
		val encoded = coding.encode(text)
		val data = ByteArray(encoded.remaining())
		for (i in 0 until data.size) data[i] = encoded.get()
		return write(data)
	}
}