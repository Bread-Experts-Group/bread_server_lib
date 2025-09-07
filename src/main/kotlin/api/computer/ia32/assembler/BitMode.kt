package org.bread_experts_group.api.computer.ia32.assembler

import org.bread_experts_group.coder.Mappable

enum class BitMode(override val id: Int, override val tag: String) : Mappable<BitMode, Int> {
	BITS_8(8, "8-bit"),
	BITS_16(16, "16-bit"),
	BITS_32(32, "32-bit"),
	BITS_64(64, "64-bit");

	fun range(from: String) = when (this) {
		BITS_8 -> if (from.startsWith('-')) Byte.MIN_VALUE..0L
		else 0..(UByte.MAX_VALUE.toLong())

		BITS_16 -> if (from.startsWith('-')) Short.MIN_VALUE..0L
		else 0..(UShort.MAX_VALUE.toLong())

		BITS_32 -> if (from.startsWith('-')) Int.MIN_VALUE..0L
		else 0..(UInt.MAX_VALUE.toLong())

		else -> throw UnsupportedOperationException(this.name)
	}

	override fun toString(): String = stringForm()
}