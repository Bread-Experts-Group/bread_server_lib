package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.feature.SystemDevicePathAppendFeature
import org.bread_experts_group.ffi.windows.decodeWin32Error
import org.bread_experts_group.ffi.windows.nativePathCchAppendEx
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsSystemDevicePathAppendFeature(pathSegment: MemorySegment) : SystemDevicePathAppendFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativePathCchAppendEx != null

	private val localArena = Arena.ofConfined()
	private val pathSegment = localArena.allocate(pathSegment.byteSize()).copyFrom(pathSegment)
	override fun append(element: String): SystemDevice = Arena.ofConfined().use { tempArena ->
		val append = tempArena.allocateFrom(element, Charsets.UTF_16LE)
		val buffer = tempArena.allocate((pathSegment.byteSize() + (element.length * 2)) + 4)
		buffer.copyFrom(pathSegment)
		decodeWin32Error(
			nativePathCchAppendEx!!.invokeExact(
				buffer,
				buffer.byteSize() / 2,
				append,
				0x00000003 // TODO PathCchAppendEx flags
			) as Int
		)
		return createPathDevice(buffer)
	}
}