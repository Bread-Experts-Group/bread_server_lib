package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.feature.SystemGetPathDeviceDirectFeature
import org.bread_experts_group.ffi.autoArena

class WindowsSystemGetPathDeviceDirectFeature : SystemGetPathDeviceDirectFeature(
	ImplementationSource.SYSTEM_NATIVE,
	SystemFeatures.GET_PATH_DEVICE_DIRECT
) {
	override fun supported(): Boolean = true
	override fun get(device: String): SystemDevice {
		val filePathSegment = autoArena.allocateFrom(device, Charsets.UTF_16LE)
		return winCreatePathDevice(filePathSegment)
	}
}