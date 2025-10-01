package org.bread_experts_group.api.secure.blob.feature.windows

import org.bread_experts_group.api.secure.blob.feature.CrossProcessEncryptedSecureDataBlobFeature
import org.bread_experts_group.api.secure.blob.windows.WindowsSecureDataBlob
import org.bread_experts_group.ffi.windows.WindowsCryptProtectMemoryFlags

class WindowsCrossProcessEncryptedSecureDataBlobFeature(
	private val parent: WindowsSecureDataBlob
) : CrossProcessEncryptedSecureDataBlobFeature() {
	override fun supported(): Boolean = true

	override fun initialize(size: Long) = initializeOff(
		parent, size,
		WindowsCryptProtectMemoryFlags.CRYPTPROTECTMEMORY_CROSS_PROCESS
	)
}