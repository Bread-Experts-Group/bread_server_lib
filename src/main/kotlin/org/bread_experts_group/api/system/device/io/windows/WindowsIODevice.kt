package org.bread_experts_group.api.system.device.io.windows

import org.bread_experts_group.api.system.device.io.IODevice
import org.bread_experts_group.api.system.device.io.IODeviceFeatureImplementation
import java.lang.foreign.MemorySegment

class WindowsIODevice(handle: MemorySegment) : IODevice() {
	override val features: MutableList<IODeviceFeatureImplementation<*>> = mutableListOf(
		WindowsIODeviceWriteFeature(handle),
		WindowsIODeviceReadFeature(handle),
		WindowsIODeviceReopenFeature(handle),
		WindowsIODeviceFlushFeature(handle),
		WindowsIODeviceSeekFeature(handle),
		WindowsIODeviceDataRangeLockFeature(handle)
	)
}