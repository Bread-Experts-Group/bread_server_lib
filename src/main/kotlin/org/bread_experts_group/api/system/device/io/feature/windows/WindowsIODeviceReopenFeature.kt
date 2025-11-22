package org.bread_experts_group.api.system.device.io.feature.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.io.IODevice
import org.bread_experts_group.api.system.device.io.feature.IODeviceReopenFeature
import org.bread_experts_group.api.system.device.io.open.ReOpenIODeviceFeatureIdentifier
import org.bread_experts_group.api.system.device.io.windows.WindowsIODevice
import org.bread_experts_group.api.system.device.windows.WindowsSystemDeviceIODeviceFeature.Companion.getDesiredAccess
import org.bread_experts_group.api.system.device.windows.WindowsSystemDeviceIODeviceFeature.Companion.getFlags
import org.bread_experts_group.api.system.device.windows.WindowsSystemDeviceIODeviceFeature.Companion.getShareMode
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.INVALID_HANDLE_VALUE
import org.bread_experts_group.ffi.windows.nativeCloseHandle
import org.bread_experts_group.ffi.windows.nativeReOpenFile
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.MemorySegment

class WindowsIODeviceReopenFeature(private val handle: MemorySegment) : IODeviceReopenFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeReOpenFile != null

	override fun reopen(
		vararg features: ReOpenIODeviceFeatureIdentifier
	): Pair<IODevice, List<ReOpenIODeviceFeatureIdentifier>> {
		val supportedFeatures = mutableListOf<ReOpenIODeviceFeatureIdentifier>()
		val handle = nativeReOpenFile!!.invokeExact(
			capturedStateSegment,
			handle,
			getDesiredAccess(features, supportedFeatures),
			getShareMode(features, supportedFeatures),
			getFlags(features, supportedFeatures)
		) as MemorySegment
		if (handle == INVALID_HANDLE_VALUE) throwLastError()
		val newDevice = WindowsIODevice(handle)
		newDevice.features.add(
			WindowsIODeviceReleaseFeature(newDevice.registerCleaningAction {
				if (nativeCloseHandle!!.invokeExact(capturedStateSegment, handle) as Int == 0)
					throwLastError()
			})
		)
		return newDevice to supportedFeatures
	}
}