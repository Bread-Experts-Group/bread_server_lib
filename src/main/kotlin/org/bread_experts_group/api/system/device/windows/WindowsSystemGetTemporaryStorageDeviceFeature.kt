package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.feature.SystemGetPathDeviceFeature
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.nativeGetTempPath2W
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsSystemGetTemporaryStorageDeviceFeature : SystemGetPathDeviceFeature(
	ImplementationSource.SYSTEM_NATIVE,
	SystemFeatures.GET_TEMPORARY_STORAGE_PATH_DEVICE
) {
	override fun supported(): Boolean = nativeGetTempPath2W != null
	override val device: SystemDevice
		get() {
			var size = nativeGetTempPath2W!!.invokeExact(capturedStateSegment, 0, MemorySegment.NULL) as Int
			if (size == 0) throwLastError()
			val filePathArena = Arena.ofShared()
			val filePathSegment = filePathArena.allocate(size * 2L)
			size = nativeGetTempPath2W.invokeExact(capturedStateSegment, size, filePathSegment) as Int
			if (size == 0) throwLastError()
			return createPathDevice(filePathArena, filePathSegment)
		}
}