package org.bread_experts_group.api.system.device.io.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.io.feature.IODeviceWriteFeature
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.ffi.windows.nativeWriteFile
import org.bread_experts_group.ffi.windows.threadLocalDWORD0
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.MemorySegment

class WindowsIODeviceWriteFeature(
	private val handle: MemorySegment
) : IODeviceWriteFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean {
		threadLocalDWORD0.set(DWORD, 0, 0)
		val status = (nativeWriteFile ?: return false).invokeExact(
			capturedStateSegment,
			handle,
			threadLocalDWORD0,
			0,
			threadLocalDWORD0,
			MemorySegment.NULL
		) as Int
		return status != 0
	}

	override fun write(from: MemorySegment): Int {
		val status = nativeWriteFile!!.invokeExact(
			capturedStateSegment,
			handle,
			from,
			from.byteSize().toInt(),
			threadLocalDWORD0,
			MemorySegment.NULL
		) as Int
		if (status == 0) throwLastError()
		return threadLocalDWORD0.get(DWORD, 0)
	}
}