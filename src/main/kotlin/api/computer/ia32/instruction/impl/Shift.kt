package org.bread_experts_group.api.computer.ia32.instruction.impl

import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.api.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.api.computer.ia32.instruction.type.flag.ArithmeticFlagOperations.Companion.HIGH_16
import org.bread_experts_group.api.computer.ia32.instruction.type.flag.ArithmeticFlagOperations.Companion.HIGH_32
import org.bread_experts_group.api.computer.ia32.instruction.type.flag.ArithmeticFlagOperations.Companion.HIGH_8
import org.bread_experts_group.api.computer.ia32.register.FlagsRegister.FlagType

fun shl32(processor: IA32Processor, a: UInt, n: Int): UInt {
	var r = a
	val masked = n and 0x1F
	repeat(masked) {
		processor.flags.setFlag(FlagType.CARRY_FLAG, (r and HIGH_32) > 0u)
		r = r * 2u
	}
	if (masked == 1)
		processor.flags.setFlag(FlagType.OVERFLOW_FLAG, ((r and HIGH_32) xor carry10i(processor)) > 0u)
	return r
}

fun shl16(processor: IA32Processor, a: UShort, n: Int): UShort {
	var r = a
	val masked = n and 0x1F
	repeat(masked) {
		processor.flags.setFlag(FlagType.CARRY_FLAG, (r and HIGH_16) > 0u)
		r = (r * 2u).toUShort()
	}
	if (masked == 1)
		processor.flags.setFlag(FlagType.OVERFLOW_FLAG, ((r and HIGH_16) xor carry10s(processor)) > 0u)
	return r
}

fun shl8(processor: IA32Processor, a: UByte, n: Int): UByte {
	var r = a
	val masked = n and 0x1F
	repeat(masked) {
		processor.flags.setFlag(FlagType.CARRY_FLAG, (r and HIGH_8) > 0u)
		r = (r * 2u).toUByte()
	}
	if (masked == 1)
		processor.flags.setFlag(FlagType.OVERFLOW_FLAG, ((r and HIGH_8) xor carry10b(processor)) > 0u)
	return r
}

fun shr32(processor: IA32Processor, a: UInt, n: Int): UInt {
	var r = a
	val masked = n and 0x1F
	repeat(masked) {
		processor.flags.setFlag(FlagType.CARRY_FLAG, (r and 1u) > 0u)
		r = r / 2u
	}
	if (masked == 1) processor.flags.setFlag(FlagType.OVERFLOW_FLAG, a > 0u)
	return r
}

fun shr16(processor: IA32Processor, a: UShort, n: Int): UShort {
	var r = a
	val masked = n and 0x1F
	repeat(masked) {
		processor.flags.setFlag(FlagType.CARRY_FLAG, (r and 1u) > 0u)
		r = (r / 2u).toUShort()
	}
	if (masked == 1) processor.flags.setFlag(FlagType.OVERFLOW_FLAG, a > 0u)
	return r
}

fun shr8(processor: IA32Processor, a: UByte, n: Int): UByte {
	var r = a
	val masked = n and 0x1F
	repeat(masked) {
		processor.flags.setFlag(FlagType.CARRY_FLAG, (r and 1u) > 0u)
		r = (r / 2u).toUByte()
	}
	if (masked == 1) processor.flags.setFlag(FlagType.OVERFLOW_FLAG, a > 0u)
	return r
}

fun sar32(processor: IA32Processor, a: UInt, n: Int): UInt {
	var r = a
	val masked = n and 0x1F
	repeat(masked) {
		processor.flags.setFlag(FlagType.CARRY_FLAG, (r and 1u) > 0u)
		r = (r.toInt() / 2).toUInt()
	}
	if (masked == 1) processor.flags.setFlag(FlagType.OVERFLOW_FLAG, false)
	return r
}

fun sar16(processor: IA32Processor, a: UShort, n: Int): UShort {
	var r = a
	val masked = n and 0x1F
	repeat(masked) {
		processor.flags.setFlag(FlagType.CARRY_FLAG, (r and 1u) > 0u)
		r = (r.toShort() / 2).toUShort()
	}
	if (masked == 1) processor.flags.setFlag(FlagType.OVERFLOW_FLAG, false)
	return r
}

fun sar8(processor: IA32Processor, a: UByte, n: Int): UByte {
	var r = a
	val masked = n and 0x1F
	repeat(masked) {
		processor.flags.setFlag(FlagType.CARRY_FLAG, (r and 1u) > 0u)
		r = (r.toByte() / 2).toUByte()
	}
	if (masked == 1) processor.flags.setFlag(FlagType.OVERFLOW_FLAG, false)
	return r
}

class Shift {
	sealed class TwoOperand8Bit(
		s: String,
		val op8: (IA32Processor, UByte, Int) -> UByte,
		val d: () -> String,
		val dc: () -> Pair<Pair<() -> UByte, (UByte) -> Unit>, () -> Int>
	) : Instruction(0u, "s$s") {
		override fun operands(processor: IA32Processor): String = d()
		override fun handle(processor: IA32Processor) {
			val (dest, src) = dc()
			val result = op8(processor, dest.first(), src())
			dest.second(result)
			setFlagsSFZFPF8(processor, result)
		}
	}

	class Left8Bit(
		d: () -> String,
		dc: () -> Pair<Pair<() -> UByte, (UByte) -> Unit>, () -> Int>
	) : TwoOperand8Bit("hl", ::shl8, d, dc)

	class Right8Bit(
		d: () -> String,
		dc: () -> Pair<Pair<() -> UByte, (UByte) -> Unit>, () -> Int>
	) : TwoOperand8Bit("hr", ::shr8, d, dc)

	class RightArithmetic8Bit(
		d: () -> String,
		dc: () -> Pair<Pair<() -> UByte, (UByte) -> Unit>, () -> Int>
	) : TwoOperand8Bit("ar", ::sar8, d, dc)

	sealed class TwoOperand(
		s: String,
		val op16: (IA32Processor, UShort, Int) -> UShort,
		val op32: (IA32Processor, UInt, Int) -> UInt,
		val d16: () -> String,
		val dc16: () -> Pair<Pair<() -> UShort, (UShort) -> Unit>, () -> Int>,
		val d32: () -> String,
		val dc32: () -> Pair<Pair<() -> UInt, (UInt) -> Unit>, () -> Int>
	) : Instruction(0u, "s$s") {
		override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
			AddressingLength.R16 -> d16()
			AddressingLength.R32 -> d32()
			else -> throw UnsupportedOperationException()
		}

		override fun handle(processor: IA32Processor) {
			when (processor.operandSize) {
				AddressingLength.R16 -> {
					val (dest16, src) = dc16()
					val result = op16(processor, dest16.first(), src())
					dest16.second(result)
					setFlagsSFZFPF16(processor, result)
				}

				AddressingLength.R32 -> {
					val (dest32, src) = dc32()
					val result = op32(processor, dest32.first(), src())
					dest32.second(result)
					setFlagsSFZFPF32(processor, result)
				}

				else -> throw UnsupportedOperationException()
			}
		}
	}

	class Left(
		d16: () -> String,
		dc16: () -> Pair<Pair<() -> UShort, (UShort) -> Unit>, () -> Int>,
		d32: () -> String,
		dc32: () -> Pair<Pair<() -> UInt, (UInt) -> Unit>, () -> Int>
	) : TwoOperand("hl", ::shl16, ::shl32, d16, dc16, d32, dc32)

	class Right(
		d16: () -> String,
		dc16: () -> Pair<Pair<() -> UShort, (UShort) -> Unit>, () -> Int>,
		d32: () -> String,
		dc32: () -> Pair<Pair<() -> UInt, (UInt) -> Unit>, () -> Int>
	) : TwoOperand("hr", ::shr16, ::shr32, d16, dc16, d32, dc32)

	class RightArithmetic(
		d16: () -> String,
		dc16: () -> Pair<Pair<() -> UShort, (UShort) -> Unit>, () -> Int>,
		d32: () -> String,
		dc32: () -> Pair<Pair<() -> UInt, (UInt) -> Unit>, () -> Int>
	) : TwoOperand("ar", ::sar16, ::sar32, d16, dc16, d32, dc32)
}