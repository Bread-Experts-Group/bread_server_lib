package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.feature.SystemGetCurrentWorkingDeviceFeature
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.nativeGetCurrentDirectoryW
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsSystemGetCurrentWorkingDeviceFeature : SystemGetCurrentWorkingDeviceFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeGetCurrentDirectoryW != null
	override val device: SystemDevice
		get() = Arena.ofConfined().use { tempArena ->
			var size = nativeGetCurrentDirectoryW!!.invokeExact(capturedStateSegment, 0, MemorySegment.NULL) as Int
			if (size == 0) throwLastError()
			val filePathSegment = tempArena.allocate(size * 2L)
			size = nativeGetCurrentDirectoryW.invokeExact(capturedStateSegment, size, filePathSegment) as Int
			if (size == 0) throwLastError()
			createPathDevice(filePathSegment)
		}
}