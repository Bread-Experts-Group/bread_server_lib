package org.bread_experts_group.api.secure.cryptography.feature.hash

interface MACCommon {
	fun setSecret(key: ByteArray)
}