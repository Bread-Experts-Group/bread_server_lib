package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.feature.SystemSetPathDeviceFeature
import org.bread_experts_group.ffi.autoArena
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.nativeSetCurrentDirectoryWide
import org.bread_experts_group.ffi.windows.throwLastError
import org.bread_experts_group.ffi.windows.winCharsetWide

class WindowsSystemSetCurrentWorkingDeviceFeature : SystemSetPathDeviceFeature(
	ImplementationSource.SYSTEM_NATIVE,
	SystemFeatures.SET_CURRENT_WORKING_PATH_DEVICE
) {
	override fun supported(): Boolean = nativeSetCurrentDirectoryWide != null
	override fun set(device: SystemDevice) {
		val pathSegment = autoArena.allocateFrom(
			device.get(SystemDeviceFeatures.PATH).element,
			winCharsetWide
		)
		val status = nativeSetCurrentDirectoryWide!!.invokeExact(
			capturedStateSegment,
			pathSegment
		) as Int
		if (status == 0) throwLastError()
	}
}