package org.bread_experts_group.api.io.feature.device.feature.windows

import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.io.feature.device.feature.IODeviceReadFeature
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.ffi.windows.decodeLastError
import org.bread_experts_group.ffi.windows.nativeReadFile
import org.bread_experts_group.ffi.windows.threadLocalDWORD0
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class WindowsIODeviceReadFeature(private val handle: MemorySegment) : IODeviceReadFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE

	// TODO: Figure out a better way to ascertain read.
	override fun supported(): Boolean = true

	override fun read(into: MemorySegment, length: Int): Int {
		val status = nativeReadFile!!.invokeExact(
			capturedStateSegment,
			handle,
			into,
			length,
			threadLocalDWORD0,
			MemorySegment.NULL
		) as Int
		if (status == 0) decodeLastError()
		return threadLocalDWORD0.get(DWORD, 0)
	}

	override fun read(into: ByteArray, offset: Int, length: Int): Int = Arena.ofConfined().use {
		val allocated = it.allocate(length.toLong())
		val read = read(allocated, length)
		MemorySegment.copy(allocated, ValueLayout.JAVA_BYTE, 0, into, 0, length)
		read
	}
}