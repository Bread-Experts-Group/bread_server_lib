package org.bread_experts_group.api.system.io.linux

import org.bread_experts_group.api.system.io.IODevice
import org.bread_experts_group.api.system.io.IODeviceFeatureImplementation

class LinuxIODevice(handle: Int) : IODevice() {
	override val features: MutableList<IODeviceFeatureImplementation<*>> = mutableListOf(
		LinuxIODeviceWriteFeature(handle),
		LinuxIODeviceReadFeature(handle),
//		WindowsIODeviceReopenFeature(handle),
//		WindowsIODeviceFlushFeature(handle),
//		WindowsIODeviceSeekFeature(handle),
//		WindowsIODeviceDataRangeLockFeature(handle)
	)
}