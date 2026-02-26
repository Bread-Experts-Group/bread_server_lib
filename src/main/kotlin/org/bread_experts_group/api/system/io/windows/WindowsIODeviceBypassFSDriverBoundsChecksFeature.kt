package org.bread_experts_group.api.system.io.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.io.feature.IODeviceBypassFSDriverBoundsChecksFeature
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.ioctl.FSCTL_ALLOW_EXTENDED_DASD_IO
import org.bread_experts_group.ffi.windows.nativeDeviceIoControl
import org.bread_experts_group.ffi.windows.threadLocalDWORD0
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.MemorySegment

class WindowsIODeviceBypassFSDriverBoundsChecksFeature(
	private val handle: MemorySegment
) : IODeviceBypassFSDriverBoundsChecksFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeDeviceIoControl != null

	override fun bypass() {
		val status = nativeDeviceIoControl!!.invokeExact(
			capturedStateSegment,
			handle,
			FSCTL_ALLOW_EXTENDED_DASD_IO,
			MemorySegment.NULL,
			0,
			MemorySegment.NULL,
			0,
			threadLocalDWORD0,
			MemorySegment.NULL
		) as Int
		if (status == 0) throwLastError()
	}
}