package org.bread_experts_group.api.secure.cryptography.windows.feature.hash

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.secure.cryptography.feature.hash.XOFHashingFeature
import org.bread_experts_group.ffi.windows.WindowsNTSTATUSException
import org.bread_experts_group.ffi.windows.bcrypt.WindowsBCryptAlgorithmFlags
import org.bread_experts_group.ffi.windows.bcrypt.nativeBCryptFinishHash
import org.bread_experts_group.ffi.windows.returnsNTSTATUS
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

open class WindowsXOFHashingFeature(
	override val expresses: FeatureExpression<XOFHashingFeature>,
	val algorithm: MemorySegment,
	private val arena: Arena = Arena.ofShared()
) : XOFHashingFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE

	var internalHash: MemorySegment? = null
	val hash: MemorySegment
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

	override fun export(): ByteArray = Arena.ofConfined().use { tempArena ->
		exportX(hashGetDigestLength(algorithm, tempArena))
	}

	override fun exportX(length: Int): ByteArray {
		val duped = dupeHash(hash)
		val exported = flushX(length)
		internalHash = duped
		return exported
	}

	override fun flush(): ByteArray = hashFlush(algorithm, hash)
	override fun flushX(length: Int): ByteArray = hashFlush(algorithm, hash, length)

	override fun exportIncremental(length: Int): ByteArray = Arena.ofConfined().use { tempArena ->
		val allocated = tempArena.allocate(length.toLong())
		nativeBCryptFinishHash!!.returnsNTSTATUS(
			hash,
			allocated,
			allocated.byteSize().toInt(),
			WindowsBCryptAlgorithmFlags.BCRYPT_HASH_DONT_RESET_FLAG.position.toInt()
		)
		allocated.toArray(ValueLayout.JAVA_BYTE)
	}

	override fun reset() {
		internalHash = null
	}
}