package org.bread_experts_group.api.secure.cryptography.feature.symencrypt

import org.bread_experts_group.api.secure.blob.SecuredByteArray

class AESKey(val key: SecuredByteArray) {
	init {
		if (key.around.size !in intArrayOf(16, 24, 32))
			throw IllegalArgumentException("Key size must be 128/192/256 bits")
	}
}