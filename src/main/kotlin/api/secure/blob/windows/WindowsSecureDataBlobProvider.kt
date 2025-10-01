package org.bread_experts_group.api.secure.blob.windows

import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.secure.blob.SecureDataBlob
import org.bread_experts_group.api.secure.blob.SecureDataBlobProvider
import org.bread_experts_group.ffi.windows.nativeCryptProtectMemory
import org.bread_experts_group.ffi.windows.nativeCryptUnprotectMemory

class WindowsSecureDataBlobProvider : SecureDataBlobProvider() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean {
		return nativeCryptProtectMemory != null && nativeCryptUnprotectMemory != null
	}

	override fun new(): SecureDataBlob = WindowsSecureDataBlob()
}