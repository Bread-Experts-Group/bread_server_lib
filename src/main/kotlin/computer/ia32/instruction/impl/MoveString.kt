package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.type.Instruction

class MoveString : InstructionCluster {
	object ZeroOperand8Bit : Instruction(0xA4u, "movs") {
		override fun operands(processor: IA32Processor): String = ""
		override fun handle(processor: IA32Processor) {
			processor.computer.setMemoryAt(
				esDIOffset(processor),
				processor.computer.getMemoryAt(sSIOffset(processor))
			)
			moveSIDI(processor, 1u)
		}
	}

	object ZeroOperand : Instruction(0xA5u, "movs") {
		override fun operands(processor: IA32Processor): String = ""
		override fun handle(processor: IA32Processor) = when (processor.operandSize) {
			AddressingLength.R32 -> {
				processor.computer.setMemoryAt32(
					esDIOffset(processor),
					processor.computer.getMemoryAt32(sSIOffset(processor))
				)
				moveSIDI(processor, 4u)
			}

			AddressingLength.R16 -> {
				processor.computer.setMemoryAt16(
					esDIOffset(processor),
					processor.computer.getMemoryAt16(sSIOffset(processor))
				)
				moveSIDI(processor, 2u)
			}

			else -> throw UnsupportedOperationException()
		}
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		ZeroOperand8Bit,
		ZeroOperand
	)
}