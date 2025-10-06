package org.bread_experts_group.api.secure.cryptography.windows.feature.hash

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.secure.cryptography.CryptographySystemFeatures
import org.bread_experts_group.api.secure.cryptography.feature.hash.HashingMACSIMDFeature
import org.bread_experts_group.ffi.windows.WindowsNTSTATUSException
import org.bread_experts_group.ffi.windows.bcrypt.*
import org.bread_experts_group.ffi.windows.returnsNTSTATUS
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class WindowsMACSIMDHashingFeature(
	override val expresses: FeatureExpression<HashingMACSIMDFeature>,
	private val algorithm: MemorySegment,
	private val arena: Arena = Arena.ofShared()
) : HashingMACSIMDFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	private var internalHash: MemorySegment? = null
	private val hash: MemorySegment
		get() = internalHash ?: throw IllegalStateException("Secret not initialized")
	private var size: Int = 0

	override fun supported(): Boolean {
		try {
			start(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16), 3)
			add(byteArrayOf(4, 5, 6))
			flush()
			internalHash = null
		} catch (_: WindowsNTSTATUSException) {
			return false
		}
		return true
	}

	override fun start(key: ByteArray, n: Int) {
		if (expresses == CryptographySystemFeatures.HASHING_AES_CMAC_SIMD && key.size !in intArrayOf(16, 24, 32))
			throw IllegalArgumentException("AES-CMAC requires 128/192/256-bit key")
		internalHash = createBCryptMultiHashHandle(algorithm, arena, n, key)
		size = n
	}

	override fun add(i: IntArray, b: ByteArray) {
		TODO("Not yet implemented")
	}

	override fun add(i: IntArray, b: Array<ByteArray>) {
		TODO("Not yet implemented")
	}

	override fun add(b: ByteArray) = Arena.ofConfined().use { tempArena ->
		hash
		if (b.size != size) throw IllegalArgumentException(
			"SIMD input [${b.size} element(s)] is not equal to size of operation [$size]"
		)
		val allocated = tempArena.allocate(
			BCRYPT_MULTI_HASH_OPERATION,
			b.size.toLong()
		)
		b.forEachIndexed { index, byte ->
			val operation = allocated.asSlice(
				BCRYPT_MULTI_HASH_OPERATION.byteSize() * index,
				BCRYPT_MULTI_HASH_OPERATION
			)
			BCRYPT_MULTI_HASH_OPERATION_iHash.set(operation, 0, index)
			BCRYPT_MULTI_HASH_OPERATION_hashOperation.set(
				operation, 0,
				WindowsBCryptHashOperationType.BCRYPT_HASH_OPERATION_HASH_DATA.id.toInt()
			)
			val data = tempArena.allocateFrom(ValueLayout.JAVA_BYTE, byte)
			BCRYPT_MULTI_HASH_OPERATION_pbBuffer.set(operation, 0, data)
			BCRYPT_MULTI_HASH_OPERATION_cbBuffer.set(operation, 0, 1)
		}
		nativeBCryptProcessMultiOperations!!.returnsNTSTATUS(
			hash,
			WindowsBCryptMultiOperationType.BCRYPT_OPERATION_TYPE_HASH.id.toInt(),
			allocated,
			allocated.byteSize().toInt(),
			0
		)
	}

	override fun add(b: Array<ByteArray>) {
		TODO("Not yet implemented")
	}

	override fun flush(i: IntArray): Array<ByteArray> {
		TODO("Not yet implemented")
	}

	override fun flush(): Array<ByteArray> = Arena.ofConfined().use { tempArena ->
		hash
		val allocated = tempArena.allocate(
			BCRYPT_MULTI_HASH_OPERATION,
			size.toLong()
		)
		val digestLength = hashGetDigestLength(algorithm, tempArena)
		val buffers = Array(size) { index ->
			val operation = allocated.asSlice(
				BCRYPT_MULTI_HASH_OPERATION.byteSize() * index,
				BCRYPT_MULTI_HASH_OPERATION
			)
			BCRYPT_MULTI_HASH_OPERATION_iHash.set(operation, 0, index)
			BCRYPT_MULTI_HASH_OPERATION_hashOperation.set(
				operation, 0,
				WindowsBCryptHashOperationType.BCRYPT_HASH_OPERATION_FINISH_HASH.id.toInt()
			)
			val allocated = tempArena.allocate(digestLength.toLong())
			BCRYPT_MULTI_HASH_OPERATION_pbBuffer.set(operation, 0, allocated)
			BCRYPT_MULTI_HASH_OPERATION_cbBuffer.set(operation, 0, digestLength)
			allocated
		}
		nativeBCryptProcessMultiOperations!!.returnsNTSTATUS(
			hash,
			WindowsBCryptMultiOperationType.BCRYPT_OPERATION_TYPE_HASH.id.toInt(),
			allocated,
			allocated.byteSize().toInt(),
			0
		)
		Array(size) { index -> buffers[index].toArray(ValueLayout.JAVA_BYTE) }
	}
}