package org.bread_experts_group.api.system.io.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.windows.WindowsSystemDeviceIODeviceFeature.Companion.getDesiredAccessO
import org.bread_experts_group.api.system.device.windows.WindowsSystemDeviceIODeviceFeature.Companion.getFlags
import org.bread_experts_group.api.system.device.windows.WindowsSystemDeviceIODeviceFeature.Companion.getShareModeO
import org.bread_experts_group.api.system.device.windows.WindowsSystemDeviceIODeviceFeature.Companion.winCleanFileHandle
import org.bread_experts_group.api.system.io.IODevice
import org.bread_experts_group.api.system.io.feature.IODeviceReleaseFeature
import org.bread_experts_group.api.system.io.feature.IODeviceReopenFeature
import org.bread_experts_group.api.system.io.open.FileIOReOpenFeatures
import org.bread_experts_group.api.system.io.open.OpenIODeviceDataIdentifier
import org.bread_experts_group.api.system.io.open.ReOpenIODeviceFeatureIdentifier
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.INVALID_HANDLE_VALUE
import org.bread_experts_group.ffi.windows.nativeReOpenFile
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.MemorySegment

class WindowsIODeviceReopenFeature(private val handle: MemorySegment) : IODeviceReopenFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeReOpenFile != null

	override fun reopen(
		vararg features: ReOpenIODeviceFeatureIdentifier
	): List<OpenIODeviceDataIdentifier> {
		val data = mutableListOf<OpenIODeviceDataIdentifier>()
		val handle = nativeReOpenFile!!.invokeExact(
			capturedStateSegment,
			handle,
			getDesiredAccessO(features, data),
			getShareModeO(features, data),
			getFlags(features, data)
		) as MemorySegment
		if (handle == INVALID_HANDLE_VALUE) throwLastError()
		val newDevice = IODevice()
		newDevice.features.add(
			IODeviceReleaseFeature(
				ImplementationSource.SYSTEM_NATIVE,
				winCleanFileHandle(handle).let { { it.clean() } }
			)
		)
		val oR = data.contains(FileIOReOpenFeatures.READ)
		val oW = data.contains(FileIOReOpenFeatures.WRITE)
		if (oR || oW) newDevice.features.add(WindowsIODeviceSeekFeature(handle))
		if (oR) newDevice.features.add(WindowsIODeviceReadFeature(handle))
		if (oW) {
			newDevice.features.add(WindowsIODeviceWriteFeature(handle))
			newDevice.features.add(WindowsIODeviceFlushFeature(handle))
			newDevice.features.add(WindowsIODeviceSetSizeFeature(handle))
		}
		newDevice.features.add(WindowsIODeviceReopenFeature(handle))
		newDevice.features.add(WindowsIODeviceGetSizeFeature(handle))
		newDevice.features.add(WindowsIODeviceDataRangeLockFeature(handle))
		data.add(newDevice)
		return data
	}
}