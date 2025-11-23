package org.bread_experts_group.api.system.device.io.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.io.feature.IODeviceReadFeature
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.ffi.windows.nativeReadFile
import org.bread_experts_group.ffi.windows.threadLocalDWORD0
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.MemorySegment

class WindowsIODeviceReadFeature(private val handle: MemorySegment) : IODeviceReadFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE

	// TODO: Figure out a better way to ascertain read.
	override fun supported(): Boolean = true

	override fun read(into: MemorySegment): Int {
		val status = nativeReadFile!!.invokeExact(
			capturedStateSegment,
			handle,
			into,
			into.byteSize().coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
			threadLocalDWORD0,
			MemorySegment.NULL
		) as Int
		if (status == 0) throwLastError()
		return threadLocalDWORD0.get(DWORD, 0)
	}
}