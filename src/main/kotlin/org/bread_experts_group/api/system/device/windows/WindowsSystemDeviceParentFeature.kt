package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.feature.SystemDeviceParentFeature
import org.bread_experts_group.ffi.windows.decodeWin32Error
import org.bread_experts_group.ffi.windows.nativePathCchRemoveBackslash
import org.bread_experts_group.ffi.windows.nativePathCchRemoveFileSpec
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsSystemDeviceParentFeature(pathSegment: MemorySegment) : SystemDeviceParentFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativePathCchRemoveBackslash != null && nativePathCchRemoveFileSpec != null

	private val localArena = Arena.ofConfined()
	private val pathSegment = localArena.allocate(pathSegment.byteSize()).copyFrom(pathSegment)
	override val parent: SystemDevice by lazy {
		var status = nativePathCchRemoveBackslash!!.invokeExact(
			this.pathSegment,
			this.pathSegment.byteSize() / 2
		) as Int
		if (status != 1) decodeWin32Error(status)
		status = nativePathCchRemoveFileSpec!!.invokeExact(
			this.pathSegment,
			this.pathSegment.byteSize() / 2
		) as Int
		if (status != 1) decodeWin32Error(status)
		createPathDevice(this.pathSegment)
	}
}