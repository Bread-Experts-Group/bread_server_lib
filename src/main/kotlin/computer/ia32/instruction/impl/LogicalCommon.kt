package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.flag.ArithmeticFlagOperations.Companion.HIGH_16
import org.bread_experts_group.computer.ia32.instruction.type.flag.ArithmeticFlagOperations.Companion.HIGH_32
import org.bread_experts_group.computer.ia32.instruction.type.flag.ArithmeticFlagOperations.Companion.HIGH_8
import org.bread_experts_group.computer.ia32.register.FlagsRegister.FlagType

fun clearCFOF(processor: IA32Processor) {
	processor.flags.setFlag(FlagType.OVERFLOW_FLAG, false)
	processor.flags.setFlag(FlagType.CARRY_FLAG, false)
}

fun setFlagsSFZFPF8(processor: IA32Processor, result: UByte) {
	processor.flags.setFlag(FlagType.SIGN_FLAG, (result and HIGH_8) > UByte.MIN_VALUE)
	processor.flags.setFlag(FlagType.ZERO_FLAG, result == UByte.MIN_VALUE)
	processor.flags.setFlag(FlagType.PARITY_FLAG, (result.countOneBits() % 2) == 0)
}

fun setFlagsSFZFPF16(processor: IA32Processor, result: UShort) {
	processor.flags.setFlag(FlagType.SIGN_FLAG, (result and HIGH_16) > UShort.MIN_VALUE)
	processor.flags.setFlag(FlagType.ZERO_FLAG, result == UShort.MIN_VALUE)
	processor.flags.setFlag(FlagType.PARITY_FLAG, (result.countOneBits() % 2) == 0)
}

fun setFlagsSFZFPF32(processor: IA32Processor, result: UInt) {
	processor.flags.setFlag(FlagType.SIGN_FLAG, (result and HIGH_32) > UInt.MIN_VALUE)
	processor.flags.setFlag(FlagType.ZERO_FLAG, result == UInt.MIN_VALUE)
	processor.flags.setFlag(FlagType.PARITY_FLAG, (result.countOneBits() % 2) == 0)
}

class LogicalCommon private constructor() {
	abstract class TwoOperand8Bit(
		mnemonic: String,
		val op: (UByte, UByte) -> UByte,
		opcode: UInt,
		val d: () -> String,
		val dc: Input2<UByte>
	) : Instruction(opcode, mnemonic) {
		override fun operands(processor: IA32Processor): String = d()
		override fun handle(processor: IA32Processor) {
			val (dest, src) = dc()
			val result = op(dest.first(), src.first())
			dest.second(result)
			clearCFOF(processor)
			setFlagsSFZFPF8(processor, result)
		}
	}

	abstract class TwoOperand(
		mnemonic: String,
		val op16: (UShort, UShort) -> UShort,
		val op32: (UInt, UInt) -> UInt,
		opcode: UInt,
		val d16: () -> String,
		val dc16: Input2<UShort>,
		val d32: () -> String,
		val dc32: Input2<UInt>
	) : Instruction(opcode, mnemonic) {
		override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
			AddressingLength.R16 -> d16()
			AddressingLength.R32 -> d32()
			else -> throw UnsupportedOperationException()
		}

		override fun handle(processor: IA32Processor) {
			when (processor.operandSize) {
				AddressingLength.R16 -> {
					val (dest16, src16) = dc16()
					val result = op16(dest16.first(), src16.first())
					dest16.second(result)
					clearCFOF(processor)
					setFlagsSFZFPF16(processor, result)
				}

				AddressingLength.R32 -> {
					val (dest32, src32) = dc32()
					val result = op32(dest32.first(), src32.first())
					dest32.second(result)
					clearCFOF(processor)
					setFlagsSFZFPF32(processor, result)
				}

				else -> throw UnsupportedOperationException()
			}
		}
	}
}