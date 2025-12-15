package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.feature.SystemGetPathDeviceFeature
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.nativeGetCurrentDirectoryWide
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsSystemGetCurrentWorkingDeviceFeature : SystemGetPathDeviceFeature(
	ImplementationSource.SYSTEM_NATIVE,
	SystemFeatures.GET_CURRENT_WORKING_PATH_DEVICE
) {
	override fun supported(): Boolean = nativeGetCurrentDirectoryWide != null
	override val device: SystemDevice
		get() {
			var size = nativeGetCurrentDirectoryWide!!.invokeExact(capturedStateSegment, 0, MemorySegment.NULL) as Int
			if (size == 0) throwLastError()
			val filePathArena = Arena.ofShared()
			val filePathSegment = filePathArena.allocate(size * 2L)
			size = nativeGetCurrentDirectoryWide.invokeExact(capturedStateSegment, size, filePathSegment) as Int
			if (size == 0) throwLastError()
			return winCreatePathDevice(filePathArena, filePathSegment)
		}
}