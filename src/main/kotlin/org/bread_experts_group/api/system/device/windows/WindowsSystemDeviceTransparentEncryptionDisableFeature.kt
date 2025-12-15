@file:Suppress("LongLine")

package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.feature.SystemDeviceTransparentEncryptionDisableFeature
import org.bread_experts_group.api.system.device.transparent_encrypt.DisableTransparentEncryptionSystemDeviceFeatureIdentifier
import org.bread_experts_group.api.system.device.transparent_encrypt.WindowsTransparentEncryptionDisableFeatures
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.advapi.nativeDecryptFile
import org.bread_experts_group.ffi.windows.advapi.nativeEncryptionDisable
import org.bread_experts_group.ffi.windows.throwLastError
import org.bread_experts_group.ffi.windows.winCharsetWide
import java.lang.foreign.Arena

class WindowsSystemDeviceTransparentEncryptionDisableFeature(
	private val path: String
) : SystemDeviceTransparentEncryptionDisableFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeDecryptFile != null && nativeEncryptionDisable != null

	override fun disable(
		vararg features: DisableTransparentEncryptionSystemDeviceFeatureIdentifier
	): List<DisableTransparentEncryptionSystemDeviceFeatureIdentifier> {
		if (features.contains(WindowsTransparentEncryptionDisableFeatures.DIRECTORY)) {
			Arena.ofConfined().use { tempArena ->
				val status = nativeEncryptionDisable!!.invokeExact(
					capturedStateSegment,
					tempArena.allocateFrom(path, winCharsetWide),
					1
				) as Int
				if (status == 0) throwLastError()
			}
			return listOf(WindowsTransparentEncryptionDisableFeatures.DIRECTORY)
		} else {
			nativeDecryptFile!!(path)
			return emptyList()
		}
	}
}