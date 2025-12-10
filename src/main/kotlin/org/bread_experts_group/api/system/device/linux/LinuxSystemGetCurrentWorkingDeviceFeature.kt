package org.bread_experts_group.api.system.device.linux

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.feature.SystemGetPathDeviceFeature
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.linux.nativeGetCurrentDirName
import org.bread_experts_group.ffi.posix.nativeFree
import org.bread_experts_group.ffi.posix.throwLastErrno
import java.lang.foreign.MemorySegment

class LinuxSystemGetCurrentWorkingDeviceFeature : SystemGetPathDeviceFeature(
	ImplementationSource.SYSTEM_NATIVE,
	SystemFeatures.GET_CURRENT_WORKING_PATH_DEVICE
) {
	override fun supported(): Boolean = nativeGetCurrentDirName != null
	override val device: SystemDevice
		get() {
			val name = nativeGetCurrentDirName!!.invokeExact(capturedStateSegment) as MemorySegment
			if (name == MemorySegment.NULL) throwLastErrno()
			val nameStr = name.reinterpret(Long.MAX_VALUE).getString(0, Charsets.UTF_8)
			nativeFree!!.invokeExact(name)
			return linuxCreatePathDevice(nameStr)
		}
}