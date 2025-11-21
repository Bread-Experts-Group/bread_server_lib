package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.feature.SystemDeviceHardLinkFeature
import org.bread_experts_group.api.system.device.hardlink.HardLinkSystemDeviceFeatureIdentifier
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.nativeCreateHardLinkW
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsSystemDeviceHardLinkFeature(private val pathSegment: MemorySegment) : SystemDeviceHardLinkFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeCreateHardLinkW != null

	override fun link(
		towards: SystemDevice,
		vararg features: HardLinkSystemDeviceFeatureIdentifier
	): List<HardLinkSystemDeviceFeatureIdentifier> {
		val arena = Arena.ofConfined()
		val destinationSegment = arena.allocateFrom(
			towards.get(SystemDeviceFeatures.SYSTEM_IDENTIFIER).identity,
			Charsets.UTF_16LE
		)
		val status = nativeCreateHardLinkW!!.invokeExact(
			capturedStateSegment,
			pathSegment,
			destinationSegment,
			MemorySegment.NULL
		) as Int
		arena.close()
		if (status == 0) throwLastError()
		return emptyList()
	}
}