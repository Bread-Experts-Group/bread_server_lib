package org.bread_experts_group.api.computer.ia32.instruction.impl

import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.api.computer.ia32.instruction.type.Instruction

class OnesComplementNegation {
	class SingleOperand8Bit(
		opcode: UInt,
		val d: () -> String,
		val dc: () -> Pair<() -> UByte, (UByte) -> Unit>
	) : Instruction(opcode, "not") {
		override fun operands(processor: IA32Processor): String = d()
		override fun handle(processor: IA32Processor) {
			val dest = dc()
			dest.second(dest.first().inv())
		}
	}

	class SingleOperand(
		opcode: UInt,
		val d16: () -> String,
		val dc16: () -> Pair<() -> UShort, (UShort) -> Unit>,
		val d32: () -> String,
		val dc32: () -> Pair<() -> UInt, (UInt) -> Unit>
	) : Instruction(opcode, "not") {
		override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
			AddressingLength.R16 -> d16()
			AddressingLength.R32 -> d32()
			else -> throw UnsupportedOperationException()
		}

		override fun handle(processor: IA32Processor) {
			when (processor.operandSize) {
				AddressingLength.R16 -> {
					val dest16 = dc16()
					dest16.second(dest16.first().inv())
				}

				AddressingLength.R32 -> {
					val dest32 = dc32()
					dest32.second(dest32.first().inv())
				}

				else -> throw UnsupportedOperationException()
			}
		}
	}
}