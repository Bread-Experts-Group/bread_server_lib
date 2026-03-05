package org.bread_experts_group.api.system.io.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.windows.WindowsIODevice
import org.bread_experts_group.api.system.device.windows.WindowsSystemDeviceIODeviceFeature.Companion.getDesiredAccessO
import org.bread_experts_group.api.system.device.windows.WindowsSystemDeviceIODeviceFeature.Companion.getFlags
import org.bread_experts_group.api.system.device.windows.WindowsSystemDeviceIODeviceFeature.Companion.getShareModeO
import org.bread_experts_group.api.system.io.feature.IODeviceReopenFeature
import org.bread_experts_group.api.system.io.open.FileIOReOpenFeatures
import org.bread_experts_group.api.system.io.open.OpenIODeviceDataIdentifier
import org.bread_experts_group.api.system.io.open.ReOpenIODeviceFeatureIdentifier
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.INVALID_HANDLE_VALUE
import org.bread_experts_group.ffi.windows.nativeReOpenFile
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.MemorySegment

class WindowsIODeviceReopenFeature(
	private val device: WindowsIODevice
) : IODeviceReopenFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeReOpenFile != null

	override fun reopen(
		vararg features: ReOpenIODeviceFeatureIdentifier
	): List<OpenIODeviceDataIdentifier> {
		val data = mutableListOf<OpenIODeviceDataIdentifier>()
		val handle = nativeReOpenFile!!.invokeExact(
			capturedStateSegment,
			device.handle,
			getDesiredAccessO(features, data),
			getShareModeO(features, data),
			getFlags(features, data)
		) as MemorySegment
		if (handle == INVALID_HANDLE_VALUE) throwLastError()
		val newDevice = WindowsIODevice(handle)
		val oR = data.contains(FileIOReOpenFeatures.READ)
		val oW = data.contains(FileIOReOpenFeatures.WRITE)
		if (oR || oW) newDevice.features.add(WindowsIODeviceSeekFeature(newDevice))
		if (oR) newDevice.features.add(WindowsIODeviceReadFeature(newDevice))
		if (oW) {
			newDevice.features.add(WindowsIODeviceWriteFeature(newDevice))
			newDevice.features.add(WindowsIODeviceFlushFeature(newDevice))
			newDevice.features.add(WindowsIODeviceSetSizeFeature(newDevice))
		}
		newDevice.features.add(WindowsIOGetDeviceGeometryFeature(newDevice))
		newDevice.features.add(WindowsIODeviceBypassFSDriverBoundsChecksFeature(newDevice))
		newDevice.features.add(WindowsIODeviceGetDeviceFirmwareInfoFeature(newDevice))
		newDevice.features.add(WindowsIODeviceReopenFeature(newDevice))
		newDevice.features.add(WindowsIODeviceGetSizeFeature(newDevice))
		newDevice.features.add(WindowsIODeviceDataRangeLockFeature(newDevice))
		data.add(newDevice)
		return data
	}
}