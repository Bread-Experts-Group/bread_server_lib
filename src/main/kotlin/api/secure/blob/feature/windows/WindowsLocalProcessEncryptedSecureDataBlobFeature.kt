package org.bread_experts_group.api.secure.blob.feature.windows

import org.bread_experts_group.api.secure.blob.feature.LocalProcessEncryptedSecureDataBlobFeature
import org.bread_experts_group.api.secure.blob.windows.WindowsSecureDataBlob
import org.bread_experts_group.ffi.windows.WindowsCryptProtectMemoryFlags

class WindowsLocalProcessEncryptedSecureDataBlobFeature(
	private val parent: WindowsSecureDataBlob
) : LocalProcessEncryptedSecureDataBlobFeature() {
	override fun supported(): Boolean = true

	override fun initialize(size: Long) = initializeOff(
		parent, size,
		WindowsCryptProtectMemoryFlags.CRYPTPROTECTMEMORY_SAME_PROCESS
	)
}