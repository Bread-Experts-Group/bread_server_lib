package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.feature.SystemDeviceSoftLinkFeature
import org.bread_experts_group.api.system.device.softlink.SoftLinkSystemDeviceFeatureIdentifier
import org.bread_experts_group.api.system.device.softlink.WindowsSoftLinkSystemDeviceFeatures
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.nativeCreateSymbolicLinkW
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsSystemDeviceSoftLinkFeature(private val pathSegment: MemorySegment) : SystemDeviceSoftLinkFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeCreateSymbolicLinkW != null

	override fun link(
		towards: SystemDevice,
		vararg features: SoftLinkSystemDeviceFeatureIdentifier
	): List<SoftLinkSystemDeviceFeatureIdentifier> {
		val supportedFeatures = mutableListOf<SoftLinkSystemDeviceFeatureIdentifier>()
		val arena = Arena.ofConfined()
		val destinationSegment = arena.allocateFrom(
			towards.get(SystemDeviceFeatures.SYSTEM_IDENTIFIER).identity as String,
			Charsets.UTF_16LE
		)
		var flags = 0
		if (features.contains(WindowsSoftLinkSystemDeviceFeatures.DIRECTORY)) {
			flags = flags or 0x1
			supportedFeatures.add(WindowsSoftLinkSystemDeviceFeatures.DIRECTORY)
		}
		if (features.contains(WindowsSoftLinkSystemDeviceFeatures.UNPRIVILEGED_CREATE_DEV)) {
			flags = flags or 0x2
			supportedFeatures.add(WindowsSoftLinkSystemDeviceFeatures.UNPRIVILEGED_CREATE_DEV)
		}
		val status = nativeCreateSymbolicLinkW!!.invokeExact(
			capturedStateSegment,
			pathSegment,
			destinationSegment,
			flags
		) as Int
		arena.close()
		if (status == 0) throwLastError()
		return supportedFeatures
	}
}