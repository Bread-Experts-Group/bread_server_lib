package org.bread_experts_group.api.secure.cryptography.windows.feature

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.secure.cryptography.feature.HashingFeature
import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.ffi.windows.COMException
import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.ffi.windows.ULONG
import org.bread_experts_group.ffi.windows.WindowsNTStatus
import org.bread_experts_group.ffi.windows.bcrypt.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class WindowsHashingFeature(
	override val expresses: FeatureExpression<HashingFeature>,
	algorithmID: String,
	algorithmProvider: String
) : HashingFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = true

	protected val arena: Arena = Arena.ofShared()
	protected val algorithm: MemorySegment = Arena.ofConfined().use { tempArena ->
		val handleRec = tempArena.allocate(ValueLayout.ADDRESS)
		val status = WindowsNTStatus.entries.id(
			(nativeBCryptOpenAlgorithmProvider!!.invokeExact(
				handleRec,
				tempArena.allocateFrom(algorithmID, Charsets.UTF_16LE),
				tempArena.allocateFrom(algorithmProvider, Charsets.UTF_16LE),
				0 // TODO BCRYPT_ALG_HANDLE_HMAC_FLAG, BCRYPT_HASH_REUSABLE_FLAG
			) as Int).toUInt()
		)
		if (status.enum != WindowsNTStatus.STATUS_SUCCESS) throw COMException(status.toString())
		handleRec.get(ValueLayout.ADDRESS, 0).reinterpret(arena) {
			val status = WindowsNTStatus.entries.id(
				(nativeBCryptCloseAlgorithmProvider!!.invokeExact(
					it,
					0
				) as Int).toUInt()
			)
			if (status.enum != WindowsNTStatus.STATUS_SUCCESS) throw COMException(status.toString())
		}
	}

	private var hashObjHandle: MemorySegment? = null
		get() {
			if (field != null) return field
			Arena.ofConfined().use { tempArena ->
				val length = tempArena.allocate(DWORD)
				val lengthSz = tempArena.allocate(ULONG)
				var status = WindowsNTStatus.entries.id(
					(nativeBCryptGetProperty!!.invokeExact(
						algorithm,
						tempArena.allocateFrom("ObjectLength", Charsets.UTF_16LE),
						length,
						length.byteSize().toInt(),
						lengthSz,
						0
					) as Int).toUInt()
				)
				if (status.enum != WindowsNTStatus.STATUS_SUCCESS) throw COMException(status.toString())
				val objectAlloc = arena.allocate(length.get(DWORD, 0).toLong())
				val objectHandle = arena.allocate(ValueLayout.ADDRESS)
				status = WindowsNTStatus.entries.id(
					(nativeBCryptCreateHash!!.invokeExact(
						algorithm,
						objectHandle,
						objectAlloc,
						objectAlloc.byteSize().toInt(),
						MemorySegment.NULL,
						0,
						0 // TODO BCRYPT_HASH_REUSABLE_FLAG
					) as Int).toUInt()
				)
				if (status.enum != WindowsNTStatus.STATUS_SUCCESS) throw COMException(status.toString())
				val handle = objectHandle.get(ValueLayout.ADDRESS, 0)
				field = handle
				return handle
			}
		}
		set(value) {
			if (value != null) throw IllegalArgumentException("Not authorized to modify a hash object in use")
			val status = WindowsNTStatus.entries.id(
				(nativeBCryptDestroyHash!!.invokeExact(
					field
				) as Int).toUInt()
			)
			if (status.enum != WindowsNTStatus.STATUS_SUCCESS) throw COMException(status.toString())
			field = null
		}

	private val singleAlloc = ThreadLocal.withInitial { Arena.ofConfined().allocate(ValueLayout.JAVA_BYTE) }
	override fun plusAssign(b: Byte) {
		singleAlloc.get().set(ValueLayout.JAVA_BYTE, 0, b)
		val status = WindowsNTStatus.entries.id(
			(nativeBCryptHashData!!.invokeExact(
				hashObjHandle,
				singleAlloc.get(),
				1,
				0
			) as Int).toUInt()
		)
		if (status.enum != WindowsNTStatus.STATUS_SUCCESS) throw COMException(status.toString())
	}

	override fun plusAssign(b: ByteArray) = Arena.ofConfined().use { tempArena ->
		val copy = tempArena.allocate(b.size.toLong())
		MemorySegment.copy(b, 0, copy, ValueLayout.JAVA_BYTE, 0, b.size)
		val status = WindowsNTStatus.entries.id(
			(nativeBCryptHashData!!.invokeExact(
				hashObjHandle,
				copy,
				b.size,
				0
			) as Int).toUInt()
		)
		if (status.enum != WindowsNTStatus.STATUS_SUCCESS) throw COMException(status.toString())
	}

	override fun flush(): ByteArray = Arena.ofConfined().use { tempArena ->
		val length = tempArena.allocate(DWORD)
		val lengthSz = tempArena.allocate(ULONG)
		var status = WindowsNTStatus.entries.id(
			(nativeBCryptGetProperty!!.invokeExact(
				algorithm,
				tempArena.allocateFrom("HashDigestLength", Charsets.UTF_16LE),
				length,
				length.byteSize().toInt(),
				lengthSz,
				0
			) as Int).toUInt()
		)
		if (status.enum != WindowsNTStatus.STATUS_SUCCESS) throw COMException(status.toString())
		val allocated = tempArena.allocate(length.get(DWORD, 0).toLong())
		status = WindowsNTStatus.entries.id(
			(nativeBCryptFinishHash!!.invokeExact(
				hashObjHandle,
				allocated,
				allocated.byteSize().toInt(),
				0 // TODO BCRYPT_HASH_DONT_RESET_FLAG
			) as Int).toUInt()
		)
		if (status.enum != WindowsNTStatus.STATUS_SUCCESS) throw COMException(status.toString())
		hashObjHandle = null
		allocated.toArray(ValueLayout.JAVA_BYTE)
	}
}