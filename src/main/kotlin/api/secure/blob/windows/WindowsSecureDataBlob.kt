package org.bread_experts_group.api.secure.blob.windows

import org.bread_experts_group.api.secure.blob.SecureDataBlob
import org.bread_experts_group.api.secure.blob.SecuredByteArray
import org.bread_experts_group.api.secure.blob.feature.windows.WindowsCrossProcessEncryptedSecureDataBlobFeature
import org.bread_experts_group.api.secure.blob.feature.windows.WindowsLocalProcessEncryptedSecureDataBlobFeature
import org.bread_experts_group.api.secure.blob.feature.windows.WindowsLocalUserEncryptedSecureDataBlobFeature
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class WindowsSecureDataBlob : SecureDataBlob() {
	init {
		features.add(WindowsLocalProcessEncryptedSecureDataBlobFeature(this))
		features.add(WindowsCrossProcessEncryptedSecureDataBlobFeature(this))
		features.add(WindowsLocalUserEncryptedSecureDataBlobFeature(this))
	}

	internal val arena = Arena.ofShared()

	override var decrypt: () -> Unit = { throw IllegalStateException("Decryption not initialized") }
	override var encrypt: () -> Unit = decrypt

	override fun get(index: Long): Byte {
		decrypt()
		val r = managedSegment.get(ValueLayout.JAVA_BYTE, index)
		encrypt()
		return r
	}

	override fun get(indices: LongRange): SecuredByteArray {
		decrypt()
		val a = managedSegment.asSlice(indices.first, (indices.last - indices.first) + 1).toArray(ValueLayout.JAVA_BYTE)
		encrypt()
		return SecuredByteArray(a)
	}

	override fun set(index: Long, b: Byte) {
		decrypt()
		managedSegment.set(ValueLayout.JAVA_BYTE, index, b)
		encrypt()
	}

	override fun set(index: Long, b: ByteArray) {
		decrypt()
		MemorySegment.copy(b, 0, managedSegment, ValueLayout.JAVA_BYTE, index, b.size)
		encrypt()
	}

	override fun set(index: Long, b: SecuredByteArray) {
		this[index] = b.around
	}

	override fun set(indices: LongRange, b: Byte) {
		decrypt()
		managedSegment.asSlice(indices.first, (indices.last - indices.first) + 1).fill(b)
		encrypt()
	}

	private var internallyManagedSegment: MemorySegment? = null
	internal var managedSegmentRealSize: Long = 0
	internal var managedSegment: MemorySegment
		set(value) {
			if (internallyManagedSegment != null) throw IllegalStateException(
				"Secure data blob has already been initialized"
			)
			internallyManagedSegment = value.reinterpret(arena) {
				it.reinterpret(managedSegmentRealSize).fill(0)
			}
		}
		get() = internallyManagedSegment ?: throw IllegalStateException("The secure data blob has not been initialized")


	override fun cleanup() {
		arena.close()
		val err = { throw IllegalStateException("Secure data blob has been destroyed") }
		encrypt = err
		decrypt = err
	}

	override fun toString(): String = "SecureDataBlob[Windows-specific, ${managedSegment.byteSize()} bytes " +
			"(real size $managedSegmentRealSize bytes)]"
}