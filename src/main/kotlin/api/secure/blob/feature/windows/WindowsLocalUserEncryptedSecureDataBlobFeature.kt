package org.bread_experts_group.api.secure.blob.feature.windows

import org.bread_experts_group.api.secure.blob.feature.LocalUserEncryptedSecureDataBlobFeature
import org.bread_experts_group.api.secure.blob.windows.WindowsSecureDataBlob
import org.bread_experts_group.ffi.windows.WindowsCryptProtectMemoryFlags

class WindowsLocalUserEncryptedSecureDataBlobFeature(
	private val parent: WindowsSecureDataBlob
) : LocalUserEncryptedSecureDataBlobFeature() {
	override fun supported(): Boolean = true

	override fun initialize(size: Long) = initializeOff(
		parent, size,
		WindowsCryptProtectMemoryFlags.CRYPTPROTECTMEMORY_SAME_LOGON
	)
}