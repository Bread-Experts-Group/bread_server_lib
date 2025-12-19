package org.bread_experts_group.api.system.device.linux.x64

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.feature.SystemGetPathDeviceFeature
import org.bread_experts_group.ffi.autoArena
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.posix.linux.generic.ERANGE
import org.bread_experts_group.ffi.posix.linux.x64.nativeGetCwd
import org.bread_experts_group.ffi.posix.x64.errno
import org.bread_experts_group.ffi.posix.x64.throwLastErrno
import java.lang.foreign.MemorySegment

class LinuxX64SystemGetCurrentWorkingDeviceFeature : SystemGetPathDeviceFeature(
	ImplementationSource.SYSTEM_NATIVE,
	SystemFeatures.GET_CURRENT_WORKING_PATH_DEVICE
) {
	override fun supported(): Boolean = nativeGetCwd != null
	override val device: SystemDevice
		get() {
			var segment: MemorySegment = MemorySegment.NULL
			do {
				segment = autoArena.allocate(segment.byteSize() + 16)
				val buf = nativeGetCwd!!.invokeExact(
					capturedStateSegment,
					segment,
					segment.byteSize()
				) as MemorySegment
				if (buf == MemorySegment.NULL) {
					if (errno == ERANGE) continue
					else throwLastErrno()
				}
				break
			} while (true)
			return linuxX64CreatePathDevice(segment)
		}
}