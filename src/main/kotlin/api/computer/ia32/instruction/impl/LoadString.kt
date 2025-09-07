package org.bread_experts_group.api.computer.ia32.instruction.impl

import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.api.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.api.computer.ia32.instruction.type.Instruction

class LoadString : InstructionCluster {
	object ZeroOperand8Bit : Instruction(0xACu, "lods") {
		override fun operands(processor: IA32Processor): String = ""
		override fun handle(processor: IA32Processor) {
			processor.a.tl = processor.computer.getMemoryAt(sSIOffset(processor))
			moveSI(processor, 1u)
		}
	}

	object ZeroOperand : Instruction(0xADu, "lods") {
		override fun operands(processor: IA32Processor): String = ""
		override fun handle(processor: IA32Processor) {
			when (processor.operandSize) {
				AddressingLength.R32 -> {
					processor.a.tex = processor.computer.getMemoryAt32(sSIOffset(processor))
					moveSI(processor, 4u)
				}

				AddressingLength.R16 -> {
					processor.a.tx = processor.computer.getMemoryAt16(sSIOffset(processor))
					moveSI(processor, 2u)
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