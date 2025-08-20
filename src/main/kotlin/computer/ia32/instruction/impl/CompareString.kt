package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.type.Instruction

class CompareString : InstructionCluster {
	object ZeroOperand8Bit : Instruction(0xA6u, "cmps") {
		override fun operands(processor: IA32Processor): String = ""
		override fun handle(processor: IA32Processor) {
			setFlagsSFZFPF8(
				processor,
				subtractAndSetFlagsCFOF8(
					processor,
					processor.computer.requestMemoryAt(dsSIOffset(processor)),
					processor.computer.requestMemoryAt(esDIOffset(processor))
				)
			)
			moveSIDI(processor, 1u)
		}
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		ZeroOperand8Bit
	)
}