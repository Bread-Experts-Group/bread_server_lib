package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.feature.SystemDeviceReplaceFeature
import org.bread_experts_group.api.system.device.replace.SystemDeviceReplaceFeatureIdentifier
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.nativeReplaceFileWide
import org.bread_experts_group.ffi.windows.throwLastError
import org.bread_experts_group.ffi.windows.winCharsetWide
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsSystemDeviceReplaceFeature(private val pathSegment: MemorySegment) : SystemDeviceReplaceFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeReplaceFileWide != null

	override fun replace(
		with: SystemDevice,
		backup: SystemDevice?,
		vararg features: SystemDeviceReplaceFeatureIdentifier
	): List<SystemDeviceReplaceFeatureIdentifier> {
		val arena = Arena.ofConfined()
		val withSegment = arena.allocateFrom(
			with.get(SystemDeviceFeatures.SYSTEM_IDENTIFIER).identity as String,
			winCharsetWide
		)
		val backupSegment = if (backup != null) arena.allocateFrom(
			backup.get(SystemDeviceFeatures.SYSTEM_IDENTIFIER).identity as String,
			winCharsetWide
		) else MemorySegment.NULL
		val status = nativeReplaceFileWide!!.invokeExact(
			capturedStateSegment,
			pathSegment,
			withSegment,
			backupSegment,
			0,
			MemorySegment.NULL,
			MemorySegment.NULL
		) as Int
		arena.close()
		if (status == 0) throwLastError()
		return emptyList()
	}
}