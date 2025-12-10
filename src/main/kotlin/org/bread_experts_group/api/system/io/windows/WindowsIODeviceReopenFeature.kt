package org.bread_experts_group.api.system.io.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.windows.WindowsSystemDeviceIODeviceFeature.Companion.getDesiredAccessO
import org.bread_experts_group.api.system.device.windows.WindowsSystemDeviceIODeviceFeature.Companion.getFlags
import org.bread_experts_group.api.system.device.windows.WindowsSystemDeviceIODeviceFeature.Companion.getShareModeO
import org.bread_experts_group.api.system.io.IODevice
import org.bread_experts_group.api.system.io.feature.IODeviceReleaseFeature
import org.bread_experts_group.api.system.io.feature.IODeviceReopenFeature
import org.bread_experts_group.api.system.io.open.OpenIODeviceFeatureIdentifier
import org.bread_experts_group.api.system.io.open.ReOpenIODeviceFeatureIdentifier
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
	): Pair<IODevice, List<OpenIODeviceFeatureIdentifier>> {
		val supportedFeatures = mutableListOf<OpenIODeviceFeatureIdentifier>()
		val handle = nativeReOpenFile!!.invokeExact(
			capturedStateSegment,
			handle,
			getDesiredAccessO(features, supportedFeatures),
			getShareModeO(features, supportedFeatures),
			getFlags(features, supportedFeatures)
		) as MemorySegment
		if (handle == INVALID_HANDLE_VALUE) throwLastError()
		val newDevice = WindowsIODevice(handle)
		val cleaningAction = newDevice.registerCleaningAction {
			if (nativeCloseHandle!!.invokeExact(capturedStateSegment, handle) as Int == 0)
				throwLastError()
		}
		newDevice.features.add(
			IODeviceReleaseFeature(
				ImplementationSource.SYSTEM_NATIVE,
				cleaningAction::clean
			)
		)
		return newDevice to supportedFeatures
	}
}