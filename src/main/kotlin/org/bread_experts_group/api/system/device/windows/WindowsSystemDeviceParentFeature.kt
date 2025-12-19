package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.feature.SystemDeviceParentFeature
import org.bread_experts_group.ffi.autoArena
import org.bread_experts_group.ffi.windows.WCHAR
import org.bread_experts_group.ffi.windows.getWin32Error
import org.bread_experts_group.ffi.windows.nativePathCchRemoveBackslash
import org.bread_experts_group.ffi.windows.nativePathCchRemoveFileSpec
import java.lang.foreign.MemorySegment

class WindowsSystemDeviceParentFeature(private val pathSegment: MemorySegment) : SystemDeviceParentFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativePathCchRemoveBackslash != null && nativePathCchRemoveFileSpec != null

	override val parent: SystemDevice by lazy {
		val copied = autoArena.allocate(pathSegment.byteSize()).copyFrom(pathSegment)
		val status = nativePathCchRemoveFileSpec!!.invokeExact(
			copied,
			copied.byteSize() / WCHAR.byteSize()
		) as Int
		if (status != 1) throw getWin32Error(status) ?: IllegalStateException()
		winCreatePathDevice(copied)
	}
}