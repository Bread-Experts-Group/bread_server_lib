package org.bread_experts_group.api.secure.cryptography.windows.feature.hash

import org.bread_experts_group.coder.Flaggable.Companion.raw
import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.ffi.windows.ULONG
import org.bread_experts_group.ffi.windows.bcrypt.*
import org.bread_experts_group.ffi.windows.returnsNTRESULT
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.util.*

fun createBCryptAlgorithm(
	algorithmID: String, algorithmProvider: String,
	arena: Arena, hmac: Boolean = false
): MemorySegment = Arena.ofConfined().use { tempArena ->
	val handleRec = tempArena.allocate(ValueLayout.ADDRESS)
	val flags = EnumSet.of(WindowsBCryptAlgorithmFlags.BCRYPT_HASH_REUSABLE_FLAG)
	if (hmac) flags.add(WindowsBCryptAlgorithmFlags.BCRYPT_ALG_HANDLE_HMAC_FLAG)
	nativeBCryptOpenAlgorithmProvider!!.returnsNTRESULT(
		handleRec,
		tempArena.allocateFrom(algorithmID, Charsets.UTF_16LE),
		tempArena.allocateFrom(algorithmProvider, Charsets.UTF_16LE),
		flags.raw().toInt()
	)
	handleRec.get(ValueLayout.ADDRESS, 0).reinterpret(arena) {
		nativeBCryptCloseAlgorithmProvider!!.returnsNTRESULT(
			it,
			0
		)
	}
}

private fun secretPair(arena: Arena, secret: ByteArray?) =
	if (secret != null) arena.allocate(secret.size.toLong()).also {
		MemorySegment.copy(secret, 0, it, ValueLayout.JAVA_BYTE, 0, secret.size)
	} to secret.size else MemorySegment.NULL to 0

fun createBCryptHashHandle(
	algorithm: MemorySegment,
	arena: Arena,
	secret: ByteArray? = null
): MemorySegment = Arena.ofConfined().use { tempArena ->
	val objectHandle = tempArena.allocate(BCRYPT_HASH_HANDLE)
	val (secretAlloc, secretSize) = secretPair(tempArena, secret)
	nativeBCryptCreateHash!!.returnsNTRESULT(
		algorithm,
		objectHandle,
		MemorySegment.NULL,
		0,
		secretAlloc,
		secretSize,
		WindowsBCryptAlgorithmFlags.BCRYPT_HASH_REUSABLE_FLAG.position.toInt()
	)
	return objectHandle.get(BCRYPT_HASH_HANDLE, 0).reinterpret(arena) {
		nativeBCryptDestroyHash!!.returnsNTRESULT(it)
	}
}

fun createBCryptMultiHashHandle(
	algorithm: MemorySegment,
	arena: Arena,
	n: Int,
	secret: ByteArray? = null
): MemorySegment = Arena.ofConfined().use { tempArena ->
	val objectHandle = tempArena.allocate(BCRYPT_HASH_HANDLE)
	val (secretAlloc, secretSize) = secretPair(tempArena, secret)
	nativeBCryptCreateMultiHash!!.returnsNTRESULT(
		algorithm,
		objectHandle,
		n,
		MemorySegment.NULL,
		0,
		secretAlloc,
		secretSize,
		WindowsBCryptAlgorithmFlags.BCRYPT_HASH_REUSABLE_FLAG.position.toInt() // redundant, for consistency
	)
	return objectHandle.get(BCRYPT_HASH_HANDLE, 0).reinterpret(arena) {
		nativeBCryptDestroyHash!!.returnsNTRESULT(it)
	}
}

private val singleAlloc = ThreadLocal.withInitial { Arena.ofConfined().allocate(ValueLayout.JAVA_BYTE) }
fun hashAddSingle(b: Byte, hashHandle: MemorySegment) = singleAlloc.get().let {
	it.set(ValueLayout.JAVA_BYTE, 0, b)
	nativeBCryptHashData!!.returnsNTRESULT(
		hashHandle,
		it,
		1,
		0
	)
}

fun hashAddArray(b: ByteArray, hashHandle: MemorySegment) = Arena.ofConfined().use { tempArena ->
	val copy = tempArena.allocate(b.size.toLong())
	MemorySegment.copy(b, 0, copy, ValueLayout.JAVA_BYTE, 0, b.size)
	nativeBCryptHashData!!.returnsNTRESULT(
		hashHandle,
		copy,
		b.size,
		0
	)
}

fun hashGetDigestLength(algorithm: MemorySegment, arena: Arena): Int {
	val length = arena.allocate(DWORD)
	val lengthSz = arena.allocate(ULONG)
	nativeBCryptGetProperty!!.returnsNTRESULT(
		algorithm,
		arena.allocateFrom("HashDigestLength", Charsets.UTF_16LE),
		length,
		length.byteSize().toInt(),
		lengthSz,
		0
	)
	return length.get(DWORD, 0)
}

fun hashFlush(
	algorithm: MemorySegment,
	hashObject: MemorySegment,
	length: Int? = null
): ByteArray = Arena.ofConfined().use { tempArena ->
	val digestLength = length ?: hashGetDigestLength(algorithm, tempArena)
	val allocated = tempArena.allocate(digestLength.toLong())
	nativeBCryptFinishHash!!.returnsNTRESULT(
		hashObject,
		allocated,
		digestLength,
		0
	)
	allocated.toArray(ValueLayout.JAVA_BYTE)
}

fun dupeHash(hashObject: MemorySegment): MemorySegment = Arena.ofConfined().use { tempArena ->
	val newHandle = tempArena.allocate(BCRYPT_HASH_HANDLE)
	nativeBCryptDuplicateHash!!.returnsNTRESULT(
		hashObject,
		newHandle,
		MemorySegment.NULL,
		0,
		0
	)
	newHandle.get(BCRYPT_HASH_HANDLE, 0)
}