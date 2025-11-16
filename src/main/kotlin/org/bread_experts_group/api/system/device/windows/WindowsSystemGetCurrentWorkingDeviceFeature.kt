package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceType
import org.bread_experts_group.api.system.device.feature.SystemDeviceFriendlyNameFeature
import org.bread_experts_group.api.system.device.feature.SystemDeviceSystemIdentityFeature
import org.bread_experts_group.api.system.feature.SystemGetCurrentWorkingDeviceFeature
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.decodeLastError
import org.bread_experts_group.ffi.windows.nativeGetCurrentDirectoryW
import org.bread_experts_group.ffi.windows.nativePathFindFileNameW
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsSystemGetCurrentWorkingDeviceFeature : SystemGetCurrentWorkingDeviceFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeGetCurrentDirectoryW != null
	override val device: SystemDevice
		get() = Arena.ofConfined().use { tempArena ->
			var size = nativeGetCurrentDirectoryW!!.invokeExact(capturedStateSegment, 0, MemorySegment.NULL) as Int
			if (size == 0) decodeLastError()
			val filePathSegment = tempArena.allocate(size * 2L)
			size = nativeGetCurrentDirectoryW.invokeExact(capturedStateSegment, size, filePathSegment) as Int
			if (size == 0) decodeLastError()
			val filePath = filePathSegment.getString(0, Charsets.UTF_16LE)
			val device = SystemDevice(SystemDeviceType.FILE_SYSTEM_ENTRY)
			device.features.add(
				SystemDeviceSystemIdentityFeature(filePath, ImplementationSource.SYSTEM_NATIVE)
			)
			val fileNameSegment = nativePathFindFileNameW!!.invokeExact(filePathSegment) as MemorySegment
			val fileName = fileNameSegment.reinterpret(Long.MAX_VALUE).getString(0, Charsets.UTF_16LE)
			if (fileNameSegment != filePathSegment) device.features.add(
				SystemDeviceFriendlyNameFeature(fileName, ImplementationSource.SYSTEM_NATIVE)
			)
			device.features.add(WindowsSystemDeviceIODeviceFeature(filePathSegment))
			device
		}
}