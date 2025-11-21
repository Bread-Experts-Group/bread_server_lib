package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.delete.DeleteSystemDeviceFeatureIdentifier
import org.bread_experts_group.api.system.device.delete.StandardDeleteSystemDeviceFeatures
import org.bread_experts_group.api.system.device.feature.SystemDeviceDeleteFeature
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.nativeDeleteFile2W
import org.bread_experts_group.ffi.windows.nativeRemoveDirectoryW
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.MemorySegment

class WindowsSystemDeviceDeleteFeature(private val pathSegment: MemorySegment) : SystemDeviceDeleteFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeDeleteFile2W != null && nativeRemoveDirectoryW != null
	override fun delete(
		vararg features: DeleteSystemDeviceFeatureIdentifier
	): List<DeleteSystemDeviceFeatureIdentifier> {
		val supportedFeatures = mutableListOf<DeleteSystemDeviceFeatureIdentifier>()
		if (features.contains(StandardDeleteSystemDeviceFeatures.DIRECTORY)) {
			val status = nativeRemoveDirectoryW!!.invokeExact(
				capturedStateSegment,
				pathSegment
			) as Int
			if (status == 0) throwLastError()
			supportedFeatures.add(StandardDeleteSystemDeviceFeatures.DIRECTORY)
		} else {
			val status = nativeDeleteFile2W!!.invokeExact(
				capturedStateSegment,
				pathSegment,
				0
			) as Int
			if (status == 0) throwLastError()
		}
		return supportedFeatures
	}
}