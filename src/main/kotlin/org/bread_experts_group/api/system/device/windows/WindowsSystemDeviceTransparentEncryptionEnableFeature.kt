@file:Suppress("LongLine")

package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.feature.SystemDeviceTransparentEncryptionEnableFeature
import org.bread_experts_group.api.system.device.transparent_encrypt.EnableTransparentEncryptionSystemDeviceFeatureIdentifier
import org.bread_experts_group.api.system.device.transparent_encrypt.WindowsTransparentEncryptionEnableFeatures
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.advapi.nativeEncryptFile
import org.bread_experts_group.ffi.windows.advapi.nativeEncryptionDisable
import org.bread_experts_group.ffi.windows.throwLastError
import org.bread_experts_group.ffi.windows.winCharsetWide
import java.lang.foreign.Arena

class WindowsSystemDeviceTransparentEncryptionEnableFeature(
	private val path: String
) : SystemDeviceTransparentEncryptionEnableFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeEncryptFile != null && nativeEncryptionDisable != null
	override fun enable(
		vararg features: EnableTransparentEncryptionSystemDeviceFeatureIdentifier
	): List<EnableTransparentEncryptionSystemDeviceFeatureIdentifier> {
		if (features.contains(WindowsTransparentEncryptionEnableFeatures.DIRECTORY)) {
			Arena.ofConfined().use { tempArena ->
				val status = nativeEncryptionDisable!!.invokeExact(
					capturedStateSegment,
					tempArena.allocateFrom(path, winCharsetWide),
					0
				) as Int
				if (status == 0) throwLastError()
			}
			return listOf(WindowsTransparentEncryptionEnableFeatures.DIRECTORY)
		} else {
			nativeEncryptFile!!(path)
			return emptyList()
		}
	}
}