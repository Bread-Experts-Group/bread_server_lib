package org.bread_experts_group.api.secure.cryptography.windows.feature.random

import org.bread_experts_group.ffi.windows.bcrypt.BCryptRandomFlags
import org.bread_experts_group.ffi.windows.bcrypt.nativeBCryptGenRandom
import org.bread_experts_group.ffi.windows.returnsNTSTATUS
import java.lang.foreign.MemorySegment

fun fillRandom(algorithm: MemorySegment?, area: MemorySegment) {
	nativeBCryptGenRandom!!.returnsNTSTATUS(
		algorithm ?: MemorySegment.NULL,
		area,
		area.byteSize().toInt(),
		if (algorithm != null) 0
		else BCryptRandomFlags.BCRYPT_USE_SYSTEM_PREFERRED_RNG.position.toInt()
	)
}