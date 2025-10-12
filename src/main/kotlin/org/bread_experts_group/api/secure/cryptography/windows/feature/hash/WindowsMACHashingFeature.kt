package org.bread_experts_group.api.secure.cryptography.windows.feature.hash

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.secure.cryptography.CryptographySystemFeatures
import org.bread_experts_group.api.secure.cryptography.feature.hash.HashingMACFeature
import org.bread_experts_group.ffi.windows.WindowsNTSTATUSException
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsMACHashingFeature(
	override val expresses: FeatureExpression<HashingMACFeature>,
	private val algorithm: MemorySegment,
	private val arena: Arena = Arena.ofShared()
) : HashingMACFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	private var internalHash: MemorySegment? = null
	private val hash: MemorySegment
		get() = internalHash ?: throw IllegalStateException("Secret not initialized")

	override fun supported(): Boolean {
		try {
			setSecret(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16))
			plusAssign(1)
			flush()
			internalHash = null
		} catch (e: WindowsNTSTATUSException) {
			e.printStackTrace()
			return false
		}
		return true
	}

	override fun setSecret(key: ByteArray) {
		if (expresses == CryptographySystemFeatures.HASHING_AES_CMAC && key.size !in intArrayOf(16, 24, 32))
			throw IllegalArgumentException("AES-CMAC requires 128/192/256-bit key")
		internalHash = createBCryptHashHandle(algorithm, arena, key)
	}

	override fun plusAssign(b: Byte) = hashAddSingle(b, hash)
	override fun plusAssign(b: ByteArray) = hashAddArray(b, hash)
	override fun flush(): ByteArray = hashFlush(algorithm, hash)

	override fun export(): ByteArray {
		val duped = dupeHash(hash)
		val exported = flush()
		internalHash = duped
		return exported
	}

	override fun reset() {
		internalHash = null
	}
}