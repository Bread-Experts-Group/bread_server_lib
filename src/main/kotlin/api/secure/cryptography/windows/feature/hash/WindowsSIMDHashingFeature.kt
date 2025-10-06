package org.bread_experts_group.api.secure.cryptography.windows.feature.hash

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.secure.cryptography.feature.hash.SIMDHashingFeature
import org.bread_experts_group.ffi.windows.WindowsNTSTATUSException
import org.bread_experts_group.ffi.windows.bcrypt.*
import org.bread_experts_group.ffi.windows.returnsNTSTATUS
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class WindowsSIMDHashingFeature(
	override val expresses: FeatureExpression<SIMDHashingFeature>,
	private val algorithm: MemorySegment
) : SIMDHashingFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	private val arena: Arena = Arena.ofShared()

	private var internalHash: MemorySegment? = null
	private val hash: MemorySegment
		get() = internalHash ?: throw IllegalStateException("SIMD hashing operation not started")
	private var size = 0

	override fun supported(): Boolean {
		try {
			start(3)
			add(byteArrayOf(1, 2, 3))
			flush()
			internalHash = null
		} catch (_: WindowsNTSTATUSException) {
			return false
		}
		return true
	}

	override fun start(n: Int) {
		internalHash = createBCryptMultiHashHandle(algorithm, arena, n)
		size = n
	}

	override fun add(b: ByteArray) = Arena.ofConfined().use { tempArena ->
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

	override fun add(b: Array<ByteArray>) = Arena.ofConfined().use { tempArena ->
		if (b.size != size) throw IllegalArgumentException(
			"SIMD input [${b.size} element(s)] is not equal to size of operation [$size]"
		)
		val allocated = tempArena.allocate(
			BCRYPT_MULTI_HASH_OPERATION,
			b.size.toLong()
		)
		b.forEachIndexed { index, bytes ->
			val operation = allocated.asSlice(
				BCRYPT_MULTI_HASH_OPERATION.byteSize() * index,
				BCRYPT_MULTI_HASH_OPERATION
			)
			BCRYPT_MULTI_HASH_OPERATION_iHash.set(operation, 0, index)
			BCRYPT_MULTI_HASH_OPERATION_hashOperation.set(
				operation, 0,
				WindowsBCryptHashOperationType.BCRYPT_HASH_OPERATION_HASH_DATA.id.toInt()
			)
			val data = tempArena.allocate(ValueLayout.JAVA_BYTE, bytes.size.toLong())
			MemorySegment.copy(bytes, 0, data, ValueLayout.JAVA_BYTE, 0, bytes.size)
			BCRYPT_MULTI_HASH_OPERATION_pbBuffer.set(operation, 0, data)
			BCRYPT_MULTI_HASH_OPERATION_cbBuffer.set(operation, 0, bytes.size)
		}
		nativeBCryptProcessMultiOperations!!.returnsNTSTATUS(
			hash,
			WindowsBCryptMultiOperationType.BCRYPT_OPERATION_TYPE_HASH.id.toInt(),
			allocated,
			allocated.byteSize().toInt(),
			0
		)
	}

	override fun add(i: IntArray, b: ByteArray) = Arena.ofConfined().use { tempArena ->
		if (i.any { it >= size }) throw IllegalArgumentException("Index array contains indices out of bounds")
		val allocated = tempArena.allocate(
			BCRYPT_MULTI_HASH_OPERATION,
			b.size.toLong()
		)
		b.forEachIndexed { index, byte ->
			val operation = allocated.asSlice(
				BCRYPT_MULTI_HASH_OPERATION.byteSize() * index,
				BCRYPT_MULTI_HASH_OPERATION
			)
			BCRYPT_MULTI_HASH_OPERATION_iHash.set(operation, 0, i[index])
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

	override fun add(i: IntArray, b: Array<ByteArray>) = Arena.ofConfined().use { tempArena ->
		if (i.any { it >= size }) throw IllegalArgumentException("Index array contains indices out of bounds")
		val allocated = tempArena.allocate(
			BCRYPT_MULTI_HASH_OPERATION,
			b.size.toLong()
		)
		b.forEachIndexed { index, bytes ->
			val operation = allocated.asSlice(
				BCRYPT_MULTI_HASH_OPERATION.byteSize() * index,
				BCRYPT_MULTI_HASH_OPERATION
			)
			BCRYPT_MULTI_HASH_OPERATION_iHash.set(operation, 0, i[index])
			BCRYPT_MULTI_HASH_OPERATION_hashOperation.set(
				operation, 0,
				WindowsBCryptHashOperationType.BCRYPT_HASH_OPERATION_HASH_DATA.id.toInt()
			)
			val data = tempArena.allocate(ValueLayout.JAVA_BYTE, bytes.size.toLong())
			MemorySegment.copy(bytes, 0, data, ValueLayout.JAVA_BYTE, 0, bytes.size)
			BCRYPT_MULTI_HASH_OPERATION_pbBuffer.set(operation, 0, data)
			BCRYPT_MULTI_HASH_OPERATION_cbBuffer.set(operation, 0, bytes.size)
		}
		nativeBCryptProcessMultiOperations!!.returnsNTSTATUS(
			hash,
			WindowsBCryptMultiOperationType.BCRYPT_OPERATION_TYPE_HASH.id.toInt(),
			allocated,
			allocated.byteSize().toInt(),
			0
		)
	}

	override fun flush(i: IntArray): Array<ByteArray> = Arena.ofConfined().use { tempArena ->
		if (i.any { it >= size }) throw IllegalArgumentException("Index array contains indices out of bounds")
		val allocated = tempArena.allocate(
			BCRYPT_MULTI_HASH_OPERATION,
			i.size.toLong()
		)
		val digestLength = hashGetDigestLength(algorithm, tempArena)
		val buffers = Array(i.size) { index ->
			val operation = allocated.asSlice(
				BCRYPT_MULTI_HASH_OPERATION.byteSize() * index,
				BCRYPT_MULTI_HASH_OPERATION
			)
			BCRYPT_MULTI_HASH_OPERATION_iHash.set(operation, 0, i[index])
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
		Array(i.size) { index -> buffers[index].toArray(ValueLayout.JAVA_BYTE) }
	}

	override fun flush(): Array<ByteArray> = Arena.ofConfined().use { tempArena ->
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