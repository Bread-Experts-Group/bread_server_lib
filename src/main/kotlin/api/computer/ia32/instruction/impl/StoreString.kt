package org.bread_experts_group.api.computer.ia32.instruction.impl

import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.api.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.api.computer.ia32.instruction.type.Instruction

class StoreString : InstructionCluster {
	object ZeroOperand8Bit : Instruction(0xAAu, "stos") {
		override fun operands(processor: IA32Processor): String = ""
		override fun handle(processor: IA32Processor) {
			processor.computer.setMemoryAt(
				esDIOffset(processor),
				processor.a.tl
			)
			moveDI(processor, 1u)
		}
	}

	object ZeroOperand : Instruction(0xABu, "stos") {
		override fun operands(processor: IA32Processor): String = ""
		override fun handle(processor: IA32Processor) {
			when (processor.operandSize) {
				AddressingLength.R32 -> {
					processor.computer.setMemoryAt32(
						esDIOffset(processor),
						processor.a.tex
					)
					moveDI(processor, 4u)
				}

				AddressingLength.R16 -> {
					processor.computer.setMemoryAt16(
						esDIOffset(processor),
						processor.a.tx
					)
					moveDI(processor, 2u)
				}

				else -> throw UnsupportedOperationException()
			}
		}
	}


	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		ZeroOperand8Bit,
		ZeroOperand
	)
}