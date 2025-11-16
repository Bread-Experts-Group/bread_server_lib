package org.bread_experts_group.api.secure.cryptography.windows.feature.hash

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.secure.cryptography.feature.hash.KMACXOFHashingFeature
import org.bread_experts_group.ffi.windows.WindowsNTSTATUSException
import org.bread_experts_group.ffi.windows.bcrypt.WindowsBCryptAlgorithmFlags
import org.bread_experts_group.ffi.windows.bcrypt.nativeBCryptFinishHash
import org.bread_experts_group.ffi.windows.bcrypt.nativeBCryptSetProperty
import org.bread_experts_group.ffi.windows.returnsNTSTATUS
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class WindowsKMACXOFHashingFeature(
	override val expresses: FeatureExpression<KMACXOFHashingFeature>,
	private val algorithm: MemorySegment,
	private val arena: Arena = Arena.ofShared()
) : KMACXOFHashingFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	private var internalHash: MemorySegment? = null
	private val hash: MemorySegment
		get() = internalHash ?: throw IllegalStateException("Secret not initialized")

	override fun supported(): Boolean {
		try {
			setSecret(byteArrayOf(1, 2, 3))
			setCustomizationString(byteArrayOf(1, 2, 3))
			plusAssign(1)
			flush()
			internalHash = null
		} catch (_: WindowsNTSTATUSException) {
			return false
		}
		return true
	}

	override fun setSecret(key: ByteArray) {
		internalHash = createBCryptHashHandle(algorithm, arena, key)
	}

	override fun setCustomizationString(s: ByteArray) = Arena.ofConfined().use { tempArena ->
		val cs = tempArena.allocateFrom("CustomizationString", Charsets.UTF_16LE)
		val csA = tempArena.allocate(s.size.toLong())
		MemorySegment.copy(s, 0, csA, ValueLayout.JAVA_BYTE, 0, s.size)
		nativeBCryptSetProperty!!.returnsNTSTATUS(
			hash,
			cs,
			csA,
			s.size,
			0
		)
	}

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

	override fun plusAssign(b: Byte) = hashAddSingle(b, hash)
	override fun plusAssign(b: ByteArray) = hashAddArray(b, hash)
	override fun reset() {
		internalHash = null
	}
}