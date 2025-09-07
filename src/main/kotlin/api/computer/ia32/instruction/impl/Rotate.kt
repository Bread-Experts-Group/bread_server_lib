package org.bread_experts_group.api.computer.ia32.instruction.impl

import org.bread_experts_group.api.computer.BinaryUtil.shl
import org.bread_experts_group.api.computer.BinaryUtil.shr
import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.api.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.api.computer.ia32.instruction.type.flag.ArithmeticFlagOperations.Companion.HIGH_16
import org.bread_experts_group.api.computer.ia32.instruction.type.flag.ArithmeticFlagOperations.Companion.HIGH_32
import org.bread_experts_group.api.computer.ia32.instruction.type.flag.ArithmeticFlagOperations.Companion.HIGH_8
import org.bread_experts_group.api.computer.ia32.register.FlagsRegister.FlagType

fun rol32(processor: IA32Processor, a: UInt, n: Int): UInt {
	val n = (n and 0x1F) % 32
	var r = a
	(0..<n).forEach { i ->
		val tempCf = (r shr 31)
		r = (r * 2u) + tempCf
	}
	if (n and 0x1F != 0) processor.flags.setFlag(FlagType.CARRY_FLAG, (r and 1u) > 0u)
	if (n and 0x1F == 1) processor.flags.setFlag(FlagType.OVERFLOW_FLAG, ((r shl 31) xor carry10i(processor)) > 0u)
	return r
}

fun rol16(processor: IA32Processor, a: UShort, n: Int): UShort {
	val n = (n and 0x1F) % 16
	var r = a
	(0..<n).forEach { i ->
		val tempCf = (r shr 15)
		r = ((r * 2u) + tempCf).toUShort()
	}
	if (n and 0x1F != 0) processor.flags.setFlag(FlagType.CARRY_FLAG, (r and 1u) > 0u)
	if (n and 0x1F == 1) processor.flags.setFlag(FlagType.OVERFLOW_FLAG, ((r shl 15) xor carry10s(processor)) > 0u)
	return r
}

fun rol8(processor: IA32Processor, a: UByte, n: Int): UByte {
	val n = (n and 0x1F) % 8
	var r = a
	(0..<n).forEach { i ->
		val tempCf = (r shr 7)
		r = ((r * 2u) + tempCf).toUByte()
	}
	if (n and 0x1F != 0) processor.flags.setFlag(FlagType.CARRY_FLAG, (r and 1u) > 0u)
	if (n and 0x1F == 1) processor.flags.setFlag(FlagType.OVERFLOW_FLAG, ((r shl 7) xor carry10b(processor)) > 0u)
	return r
}

fun ror32(processor: IA32Processor, a: UInt, n: Int): UInt {
	val n = (n and 0x1F) % 32
	var r = a
	(0..<n).forEach { i ->
		val tempCf = r and 1u
		r = (r / 2u) + (tempCf shl 31)
	}
	if (n and 0x1F != 0) processor.flags.setFlag(FlagType.CARRY_FLAG, (r and HIGH_32) > 0u)
	if (n and 0x1F == 1) processor.flags.setFlag(FlagType.OVERFLOW_FLAG, ((r shr 31) xor ((r shr 30) and 1u)) > 0u)
	return r
}

fun ror16(processor: IA32Processor, a: UShort, n: Int): UShort {
	val n = (n and 0x1F) % 16
	var r = a
	(0..<n).forEach { i ->
		val tempCf = r and 1u
		r = ((r / 2u) + (tempCf shl 15)).toUShort()
	}
	if (n and 0x1F != 0) processor.flags.setFlag(FlagType.CARRY_FLAG, (r and HIGH_16) > 0u)
	if (n and 0x1F == 1) processor.flags.setFlag(FlagType.OVERFLOW_FLAG, ((r shr 15) xor ((r shr 14) and 1u)) > 0u)
	return r
}

fun ror8(processor: IA32Processor, a: UByte, n: Int): UByte {
	val n = (n and 0x1F) % 8
	var r = a
	(0..<n).forEach { i ->
		val tempCf = r and 1u
		r = ((r / 2u) + (tempCf shl 7)).toUByte()
	}
	if (n and 0x1F != 0) processor.flags.setFlag(FlagType.CARRY_FLAG, (r and HIGH_8) > 0u)
	if (n and 0x1F == 1) processor.flags.setFlag(FlagType.OVERFLOW_FLAG, ((r shr 7) xor ((r shr 6) and 1u)) > 0u)
	return r
}

fun rcl32(processor: IA32Processor, a: UInt, n: Int): UInt {
	var r = a
	val n = n and 0x1F
	(0..<n).forEach { i ->
		val tempCf = (r shr 31) > 0u
		r = (r * 2u) + carry10i(processor)
		processor.flags.setFlag(FlagType.CARRY_FLAG, tempCf)
	}
	if (n and 0x1F == 1)
		processor.flags.setFlag(FlagType.OVERFLOW_FLAG, ((r shr 31) xor carry10i(processor)) != 0u)
	return r
}

fun rcl16(processor: IA32Processor, a: UShort, n: Int): UShort {
	var r = a
	val n = (n and 0x1F) % 17
	(0..<n).forEach { i ->
		val tempCf = (r shr 15) > 0u
		r = ((r * 2u) + carry10i(processor)).toUShort()
		processor.flags.setFlag(FlagType.CARRY_FLAG, tempCf)
	}
	if (n and 0x1F == 1)
		processor.flags.setFlag(FlagType.OVERFLOW_FLAG, ((r shr 15) xor carry10s(processor)).toUInt() != 0u)
	return r
}

fun rcl8(processor: IA32Processor, a: UByte, n: Int): UByte {
	var r = a
	val n = (n and 0x1F) % 9
	(0..<n).forEach { i ->
		val tempCf = (r shr 7) > 0u
		r = ((r * 2u) + carry10b(processor)).toUByte()
		processor.flags.setFlag(FlagType.CARRY_FLAG, tempCf)
	}
	if (n and 0x1F == 1)
		processor.flags.setFlag(FlagType.OVERFLOW_FLAG, ((r shr 7) xor carry10b(processor)).toUInt() != 0u)
	return r
}

fun rcr32(processor: IA32Processor, a: UInt, n: Int): UInt {
	if (n and 0x1F == 1) processor.flags.setFlag(
		FlagType.OVERFLOW_FLAG,
		(a shr 31) xor carry10i(processor) != 0u
	)
	var r = a
	(0..<n).forEach { i ->
		val tempCf = (r and 1u) > 0u
		r = ((r / 2u) + (carry10i(processor) shl 31))
		processor.flags.setFlag(FlagType.CARRY_FLAG, tempCf)
	}
	return r
}

fun rcr16(processor: IA32Processor, a: UShort, n: Int): UShort {
	if (n and 0x1F == 1) processor.flags.setFlag(
		FlagType.OVERFLOW_FLAG,
		(a shr 15) xor carry10s(processor) != 0u.toUShort()
	)
	var r = a
	(0..<n).forEach { i ->
		val tempCf = (r and 1u) > 0u
		r = ((r / 2u) + (carry10s(processor) shl 15)).toUShort()
		processor.flags.setFlag(FlagType.CARRY_FLAG, tempCf)
	}
	return r
}

fun rcr8(processor: IA32Processor, a: UByte, n: Int): UByte {
	if (n and 0x1F == 1) processor.flags.setFlag(
		FlagType.OVERFLOW_FLAG,
		(a shr 7) xor carry10b(processor) != 0u.toUByte()
	)
	var r = a
	(0..<n).forEach { i ->
		val tempCf = (r and 1u) > 0u
		r = ((r / 2u) + (carry10b(processor) shl 7)).toUByte()
		processor.flags.setFlag(FlagType.CARRY_FLAG, tempCf)
	}
	return r
}

class Rotate {
	sealed class TwoOperand8Bit(
		s: String,
		val op8: (IA32Processor, UByte, Int) -> UByte,
		val d: () -> String,
		val dc: () -> Pair<Pair<() -> UByte, (UByte) -> Unit>, () -> Int>
	) : Instruction(0u, "r$s") {
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
	) : TwoOperand8Bit("ol", ::rol8, d, dc)

	class LeftWithCarry8Bit(
		d: () -> String,
		dc: () -> Pair<Pair<() -> UByte, (UByte) -> Unit>, () -> Int>
	) : TwoOperand8Bit("cl", ::rcl8, d, dc)

	class Right8Bit(
		d: () -> String,
		dc: () -> Pair<Pair<() -> UByte, (UByte) -> Unit>, () -> Int>
	) : TwoOperand8Bit("or", ::ror8, d, dc)

	class RightWithCarry8Bit(
		d: () -> String,
		dc: () -> Pair<Pair<() -> UByte, (UByte) -> Unit>, () -> Int>
	) : TwoOperand8Bit("cr", ::rcr8, d, dc)

	sealed class TwoOperand(
		s: String,
		val op16: (IA32Processor, UShort, Int) -> UShort,
		val op32: (IA32Processor, UInt, Int) -> UInt,
		val d16: () -> String,
		val dc16: () -> Pair<Pair<() -> UShort, (UShort) -> Unit>, () -> Int>,
		val d32: () -> String,
		val dc32: () -> Pair<Pair<() -> UInt, (UInt) -> Unit>, () -> Int>
	) : Instruction(0u, "r$s") {
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
	) : TwoOperand("ol", ::rol16, ::rol32, d16, dc16, d32, dc32)

	class LeftWithCarry(
		d16: () -> String,
		dc16: () -> Pair<Pair<() -> UShort, (UShort) -> Unit>, () -> Int>,
		d32: () -> String,
		dc32: () -> Pair<Pair<() -> UInt, (UInt) -> Unit>, () -> Int>
	) : TwoOperand("cl", ::rcl16, ::rcl32, d16, dc16, d32, dc32)

	class Right(
		d16: () -> String,
		dc16: () -> Pair<Pair<() -> UShort, (UShort) -> Unit>, () -> Int>,
		d32: () -> String,
		dc32: () -> Pair<Pair<() -> UInt, (UInt) -> Unit>, () -> Int>
	) : TwoOperand("or", ::ror16, ::ror32, d16, dc16, d32, dc32)

	class RightWithCarry(
		d16: () -> String,
		dc16: () -> Pair<Pair<() -> UShort, (UShort) -> Unit>, () -> Int>,
		d32: () -> String,
		dc32: () -> Pair<Pair<() -> UInt, (UInt) -> Unit>, () -> Int>
	) : TwoOperand("cr", ::rcr16, ::rcr32, d16, dc16, d32, dc32)
}