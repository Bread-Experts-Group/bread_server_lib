package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.type.Instruction

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

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		ZeroOperand8Bit
	)
}