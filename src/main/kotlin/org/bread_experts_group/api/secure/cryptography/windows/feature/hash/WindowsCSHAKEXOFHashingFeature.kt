package org.bread_experts_group.api.secure.cryptography.windows.feature.hash

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.secure.cryptography.CryptographySystemFeatures
import org.bread_experts_group.api.secure.cryptography.feature.hash.CSHAKEXOFHashingFeature
import org.bread_experts_group.ffi.windows.WindowsNTSTATUSException
import org.bread_experts_group.ffi.windows.bcrypt.nativeBCryptSetProperty
import org.bread_experts_group.ffi.windows.returnsNTSTATUS
import org.bread_experts_group.ffi.windows.winCharsetWide
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class WindowsCSHAKEXOFHashingFeature(
	override val expresses: FeatureExpression<CSHAKEXOFHashingFeature>,
	algorithm: MemorySegment
) : CSHAKEXOFHashingFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	private val deference = WindowsXOFHashingFeature(CryptographySystemFeatures.HASHING_SHAKE128, algorithm)
	override fun export(): ByteArray = deference.export()
	override fun exportIncremental(length: Int): ByteArray = deference.exportIncremental(length)
	override fun exportX(length: Int): ByteArray = deference.exportX(length)
	override fun flush(): ByteArray = deference.flush()
	override fun flushX(length: Int): ByteArray = deference.flushX(length)
	override fun plusAssign(b: Byte) = deference.plusAssign(b)
	override fun plusAssign(b: ByteArray) = deference.plusAssign(b)
	override fun reset() = deference.reset()

	override fun supported(): Boolean {
		try {
			setFunctionName(byteArrayOf(1, 2, 3))
			setCustomizationString(byteArrayOf(1, 2, 3))
			plusAssign(1)
			flush()
			deference.internalHash = null
		} catch (_: WindowsNTSTATUSException) {
			return false
		}
		return true
	}

	override fun setFunctionName(n: ByteArray) = Arena.ofConfined().use { tempArena ->
		val fs = tempArena.allocateFrom("FunctionNameString", winCharsetWide)
		val fsA = tempArena.allocate(n.size.toLong())
		MemorySegment.copy(n, 0, fsA, ValueLayout.JAVA_BYTE, 0, n.size)
		nativeBCryptSetProperty!!.returnsNTSTATUS(
			deference.hash,
			fs,
			fsA,
			n.size,
			0
		)
	}

	override fun setCustomizationString(s: ByteArray) = Arena.ofConfined().use { tempArena ->
		val cs = tempArena.allocateFrom("CustomizationString", winCharsetWide)
		val csA = tempArena.allocate(s.size.toLong())
		MemorySegment.copy(s, 0, csA, ValueLayout.JAVA_BYTE, 0, s.size)
		nativeBCryptSetProperty!!.returnsNTSTATUS(
			deference.hash,
			cs,
			csA,
			s.size,
			0
		)
	}
}