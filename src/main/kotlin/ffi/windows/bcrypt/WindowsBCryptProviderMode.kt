package org.bread_experts_group.ffi.windows.bcrypt

import org.bread_experts_group.coder.Mappable

enum class WindowsBCryptProviderMode(
	override val id: UInt,
	override val tag: String
) : Mappable<WindowsBCryptProviderMode, UInt> {
	CRYPT_UM(0x00000001u, "User-mode only"),
	CRYPT_KM(0x00000002u, "Kernel-mode only"),
	CRYPT_MM(0x00000003u, "Multi-mode: Must support BOTH User-mode and Kernel-mode"),
	CRYPT_ANY(0x00000004u, "Wildcard: Either User-mode, or Kernel-mode, or both");

	override fun toString(): String = stringForm()
}