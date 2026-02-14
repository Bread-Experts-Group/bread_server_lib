package org.bread_experts_group.ffi.windows.bcrypt

import org.bread_experts_group.generic.Flaggable

enum class BCryptRandomFlags(override val position: Long) : Flaggable {
	BCRYPT_USE_SYSTEM_PREFERRED_RNG(0x00000002)
}