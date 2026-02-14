package org.bread_experts_group.project_incubator.sim2

fun computeZFPFSF8(processor80386: Processor80386, n8: UByte) {
	val u32 = n8.toUInt()
	processor80386.FLAGS.ZERO = u32 == 0u
	processor80386.FLAGS.SIGN = (u32 shr 7) == 1u
	processor80386.FLAGS.PARITY = u32.countOneBits() % 2 == 0
}

fun computeZFPFSF16(processor80386: Processor80386, n16: UShort) {
	val u32 = n16.toUInt()
	processor80386.FLAGS.ZERO = u32 == 0u
	processor80386.FLAGS.SIGN = (u32 shr 15) == 1u
	processor80386.FLAGS.PARITY = (u32 and 0xFFu).countOneBits() % 2 == 0
}

fun computeZFPFSF32(processor80386: Processor80386, n32: UInt) {
	processor80386.FLAGS.ZERO = n32 == 0u
	processor80386.FLAGS.SIGN = (n32 shr 31) == 1u
	processor80386.FLAGS.PARITY = (n32 and 0xFFu).countOneBits() % 2 == 0
}

fun computeBorrow32(processor80386: Processor80386, a32: UInt, b32: UInt): UInt {
	processor80386.FLAGS.CARRY = a32 < b32
	processor80386.FLAGS.ADJUST = (a32 and 0b1111u) < (b32 and 0b1111u)
	val result = a32 - b32
	val lResult = a32.toULong() - b32
	processor80386.FLAGS.OVERFLOW = lResult != result.toULong()
	return result
}

fun computeBorrow16(processor80386: Processor80386, a16: UShort, b16: UShort): UShort {
	processor80386.FLAGS.CARRY = a16 < b16
	processor80386.FLAGS.ADJUST = (a16 and 0b1111u) < (b16 and 0b1111u)
	val result = (a16 - b16).toUShort()
	val lResult = a16.toULong() - b16
	processor80386.FLAGS.OVERFLOW = lResult != result.toULong()
	return result
}

fun computeBorrow8(processor80386: Processor80386, a8: UByte, b8: UByte): UByte {
	processor80386.FLAGS.CARRY = a8 < b8
	processor80386.FLAGS.ADJUST = (a8 and 0b1111u) < (b8 and 0b1111u)
	val result = (a8 - b8).toUByte()
	val lResult = a8.toULong() - b8
	processor80386.FLAGS.OVERFLOW = lResult != result.toULong()
	return result
}

fun computeCarry32(
	processor80386: Processor80386, a32: UInt, b32: UInt,
	setCarry: Boolean
): UInt {
	val result = a32 + b32
	if (setCarry) {
		val lResult = a32.toULong() + b32
		processor80386.FLAGS.CARRY = (lResult shr 32) and 1u != 0u.toULong()
	}
	processor80386.FLAGS.OVERFLOW = result < a32
	processor80386.FLAGS.ADJUST = (((a32 and 0b1111u) + (b32 and 0b1111u)) and 0b1_0000u) != 0u
	return result
}

fun computeCarry16(
	processor80386: Processor80386, a16: UShort, b16: UShort,
	setCarry: Boolean
): UShort {
	val result = (a16 + b16).toUShort()
	if (setCarry) {
		val lResult = a16.toUInt() + b16
		processor80386.FLAGS.CARRY = (lResult shr 16) and 1u != 0u
	}
	processor80386.FLAGS.OVERFLOW = result < a16
	processor80386.FLAGS.ADJUST = (((a16 and 0b1111u) + (b16 and 0b1111u)) and 0b1_0000u) != 0u
	return result
}