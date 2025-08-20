package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.register.FlagsRegister.FlagType

class TwosComplementNegation {
	class SingleOperand8Bit(
		opcode: UInt,
		val d: () -> String,
		val dc: () -> Pair<() -> UByte, (UByte) -> Unit>
	) : Instruction(opcode, "neg") {
		override fun operands(processor: IA32Processor): String = d()
		override fun handle(processor: IA32Processor) {
			val dest = dc()
			val tempDest = dest.first()
			processor.flags.setFlag(FlagType.CARRY_FLAG, tempDest != UByte.MIN_VALUE)
			val result = (0u - tempDest).toUByte()
			dest.second(result)
			setFlagsSFZFPF8(processor, result)
			// TODO set AF / OF
		}
	}

	class SingleOperand(
		opcode: UInt,
		val d16: () -> String,
		val dc16: () -> Pair<() -> UShort, (UShort) -> Unit>,
		val d32: () -> String,
		val dc32: () -> Pair<() -> UInt, (UInt) -> Unit>
	) : Instruction(opcode, "neg") {
		override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
			AddressingLength.R16 -> d16()
			AddressingLength.R32 -> d32()
			else -> throw UnsupportedOperationException()
		}

		override fun handle(processor: IA32Processor) {
			// TODO set AF / OF
			when (processor.operandSize) {
				AddressingLength.R16 -> {
					val dest16 = dc16()
					val tempDest = dest16.first()
					processor.flags.setFlag(FlagType.CARRY_FLAG, tempDest != UShort.MIN_VALUE)
					val result = (0u - tempDest).toUShort()
					dest16.second(result)
					setFlagsSFZFPF16(processor, result)
				}

				AddressingLength.R32 -> {
					val dest32 = dc32()
					val tempDest = dest32.first()
					processor.flags.setFlag(FlagType.CARRY_FLAG, tempDest != UInt.MIN_VALUE)
					val result = 0u - tempDest
					dest32.second(result)
					setFlagsSFZFPF32(processor, result)
				}

				else -> throw UnsupportedOperationException()
			}
		}
	}
}