package org.bread_experts_group.api.secure.cryptography.feature.random

import org.bread_experts_group.api.secure.blob.SecuredByteArray
import org.bread_experts_group.api.secure.cryptography.feature.CryptographySystemFeatureImplementation

abstract class RandomFeature : CryptographySystemFeatureImplementation<RandomFeature>() {
	abstract fun nextByte(): Byte
	fun nextUByte(): UByte = nextByte().toUByte()
	abstract fun nextShort(): Short
	fun nextUShort(): UShort = nextShort().toUShort()
	abstract fun nextInt(): Int
	fun nextUInt(): UInt = nextInt().toUInt()
	abstract fun nextLong(): Long
	fun nextULong(): ULong = nextLong().toULong()
	abstract fun fill(b: ByteArray, offset: Int = 0, length: Int = b.size)
	fun fill(s: SecuredByteArray, offset: Int = 0, length: Int = s.around.size) = fill(s.around, offset, length)
	fun nextBytes(length: Int) = ByteArray(length).also { fill(it) }
	fun nextBytesSecure(length: Int) = SecuredByteArray(nextBytes(length))
}