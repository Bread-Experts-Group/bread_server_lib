@file:Suppress("LongLine")

package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.feature.SystemDeviceTransparentEncryptionEnableFeature
import org.bread_experts_group.api.system.device.transparent_encrypt.EnableTransparentEncryptionSystemDeviceFeatureIdentifier
import org.bread_experts_group.api.system.device.transparent_encrypt.WindowsTransparentEncryptionEnableFeatures
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.nativeEncryptFileW
import org.bread_experts_group.ffi.windows.nativeEncryptionDisable
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.MemorySegment

class WindowsSystemDeviceTransparentEncryptionEnableFeature(
	private val pathSegment: MemorySegment
) : SystemDeviceTransparentEncryptionEnableFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeEncryptFileW != null && nativeEncryptionDisable != null
	override fun enable(
		vararg features: EnableTransparentEncryptionSystemDeviceFeatureIdentifier
	): List<EnableTransparentEncryptionSystemDeviceFeatureIdentifier> {
		if (features.contains(WindowsTransparentEncryptionEnableFeatures.DIRECTORY)) {
			val status = nativeEncryptionDisable!!.invokeExact(
				capturedStateSegment,
				pathSegment,
				0
			) as Int
			if (status == 0) throwLastError()
			return listOf(WindowsTransparentEncryptionEnableFeatures.DIRECTORY)
		} else {
			val status = nativeEncryptFileW!!.invokeExact(
				capturedStateSegment,
				pathSegment
			) as Int
			if (status == 0) throwLastError()
			return emptyList()
		}
	}
}