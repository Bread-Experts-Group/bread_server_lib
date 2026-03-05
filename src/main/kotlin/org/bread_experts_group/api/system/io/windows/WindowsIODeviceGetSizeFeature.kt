package org.bread_experts_group.api.system.io.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.windows.WindowsIODevice
import org.bread_experts_group.api.system.io.feature.IODeviceGetSizeFeature
import org.bread_experts_group.api.system.io.size.DataSize
import org.bread_experts_group.api.system.io.size.GetSizeIODeviceDataIdentifier
import org.bread_experts_group.api.system.io.size.GetSizeIODeviceFeatureIdentifier
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.*
import org.bread_experts_group.ffi.windows.ioctl.IOCTL_DISK_GET_LENGTH_INFO
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class WindowsIODeviceGetSizeFeature(
	private val device: WindowsIODevice
) : IODeviceGetSizeFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE

	private var usingIO = false
	override fun supported(): Boolean {
		if (nativeGetFileSizeEx != null) {
			val status = nativeGetFileSizeEx.invokeExact(
				capturedStateSegment,
				device.handle,
				threadLocalLARGE_INTEGER0
			) as Int
			if (status != 0) return true
		}
		if (nativeDeviceIoControl != null) {
			val status = nativeDeviceIoControl.invokeExact(
				capturedStateSegment,
				device.handle,
				IOCTL_DISK_GET_LENGTH_INFO,
				MemorySegment.NULL,
				0,
				threadLocalLARGE_INTEGER0,
				threadLocalLARGE_INTEGER0.byteSize().toInt(),
				threadLocalDWORD0,
				MemorySegment.NULL
			) as Int
			if (status != 0) {
				usingIO = true
				return true
			}
		}
		return false
	}

	override fun get(vararg features: GetSizeIODeviceFeatureIdentifier): List<GetSizeIODeviceDataIdentifier> {
		val status = if (!usingIO) nativeGetFileSizeEx!!.invokeExact(
			capturedStateSegment,
			device.handle,
			threadLocalLARGE_INTEGER0
		) as Int else nativeDeviceIoControl!!.invokeExact(
			capturedStateSegment,
			device.handle,
			IOCTL_DISK_GET_LENGTH_INFO,
			MemorySegment.NULL,
			0,
			threadLocalLARGE_INTEGER0,
			threadLocalLARGE_INTEGER0.byteSize().toInt(),
			threadLocalDWORD0,
			MemorySegment.NULL
		) as Int
		if (status == 0) throwLastError()
		return listOf(DataSize(threadLocalLARGE_INTEGER0.get(ValueLayout.JAVA_LONG, 0)))
	}
}