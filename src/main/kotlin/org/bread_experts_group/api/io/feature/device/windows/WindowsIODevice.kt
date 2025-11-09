package org.bread_experts_group.api.io.feature.device.windows

import org.bread_experts_group.api.io.feature.device.IODevice
import org.bread_experts_group.api.io.feature.device.IODeviceFeatureImplementation
import org.bread_experts_group.api.io.feature.device.feature.windows.WindowsIODeviceReadFeature
import org.bread_experts_group.api.io.feature.device.feature.windows.WindowsIODeviceWriteFeature
import java.lang.foreign.MemorySegment

class WindowsIODevice(handle: MemorySegment) : IODevice() {
	override val features: MutableList<IODeviceFeatureImplementation<*>> = mutableListOf(
		WindowsIODeviceWriteFeature(handle),
		WindowsIODeviceReadFeature(handle)
	)
}