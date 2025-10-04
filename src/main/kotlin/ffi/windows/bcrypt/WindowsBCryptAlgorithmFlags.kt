package org.bread_experts_group.ffi.windows.bcrypt

import org.bread_experts_group.coder.Flaggable

enum class WindowsBCryptAlgorithmFlags(override val position: Long) : Flaggable {
	BCRYPT_HASH_DONT_RESET_FLAG(0x00000001),
	BCRYPT_ALG_HANDLE_HMAC_FLAG(0x00000008),
	BCRYPT_HASH_REUSABLE_FLAG(0x00000020)
}