package org.bread_experts_group.ffi.windows.bcrypt

import org.bread_experts_group.coder.Mappable

enum class WindowsBCryptInterfaceFlags(override val id: UInt) : Mappable<WindowsBCryptInterfaceFlags, UInt> {
	CRYPT_LOCAL(0x00000001u),
	CRYPT_DOMAIN(0x00000002u);

	override val tag: String = name
	override fun toString(): String = stringForm()
}