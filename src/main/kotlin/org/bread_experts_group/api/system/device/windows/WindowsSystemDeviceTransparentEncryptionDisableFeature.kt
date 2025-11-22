@file:Suppress("LongLine")

package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.feature.SystemDeviceTransparentEncryptionDisableFeature
import org.bread_experts_group.api.system.device.transparent_encrypt.DisableTransparentEncryptionSystemDeviceFeatureIdentifier
import org.bread_experts_group.api.system.device.transparent_encrypt.WindowsTransparentEncryptionDisableFeatures
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.nativeDecryptFileW
import org.bread_experts_group.ffi.windows.nativeEncryptionDisable
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.MemorySegment

class WindowsSystemDeviceTransparentEncryptionDisableFeature(
	private val pathSegment: MemorySegment
) : SystemDeviceTransparentEncryptionDisableFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeDecryptFileW != null

	override fun disable(
		vararg features: DisableTransparentEncryptionSystemDeviceFeatureIdentifier
	): List<DisableTransparentEncryptionSystemDeviceFeatureIdentifier> {
		if (features.contains(WindowsTransparentEncryptionDisableFeatures.DIRECTORY)) {
			val status = nativeEncryptionDisable!!.invokeExact(
				capturedStateSegment,
				pathSegment,
				1
			) as Int
			if (status == 0) throwLastError()
			return listOf(WindowsTransparentEncryptionDisableFeatures.DIRECTORY)
		} else {
			val status = nativeDecryptFileW!!.invokeExact(
				capturedStateSegment,
				pathSegment,
				0
			) as Int
			if (status == 0) throwLastError()
			return emptyList()
		}
	}
}