package org.bread_experts_group.api.secure.cryptography.windows.feature.hash

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.secure.cryptography.feature.hash.HashingFeature
import org.bread_experts_group.ffi.windows.WindowsNTSTATUSException
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsHashingFeature(
	override val expresses: FeatureExpression<HashingFeature>,
	private val algorithm: MemorySegment
) : HashingFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE

	private val arena: Arena = Arena.ofShared()
	private var internalHash: MemorySegment? = null
	private val hash: MemorySegment
		get() = internalHash ?: run {
			val newHash = createBCryptHashHandle(algorithm, arena)
			internalHash = newHash
			newHash
		}

	override fun supported(): Boolean {
		try {
			plusAssign(1)
			flush()
			internalHash = null
		} catch (_: WindowsNTSTATUSException) {
			return false
		}
		return true
	}

	override fun plusAssign(b: Byte) = hashAddSingle(b, hash)
	override fun plusAssign(b: ByteArray) = hashAddArray(b, hash)
	override fun flush(): ByteArray = hashFlush(algorithm, hash)

	override fun export(): ByteArray = Arena.ofConfined().use { tempArena ->
		val duped = dupeHash(hash)
		val exported = flush()
		internalHash = duped
		return exported
	}

	override fun reset() {
		internalHash = null
	}
}