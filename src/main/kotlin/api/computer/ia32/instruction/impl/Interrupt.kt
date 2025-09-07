package org.bread_experts_group.api.computer.ia32.instruction.impl

import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.api.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.api.computer.ia32.instruction.type.imm8
import org.bread_experts_group.api.computer.ia32.register.FlagsRegister.FlagType
import org.bread_experts_group.hex

class Interrupt : InstructionCluster {
	object OneOperand : Instruction(0xCDu, "int") {
		override fun operands(processor: IA32Processor): String = hex(processor.imm8())
		override fun handle(processor: IA32Processor) {
			processor.initiateInterrupt(processor.imm8())
		}
	}

	object ZeroOperandOverflow : Instruction(0xCEu, "into") {
		override fun operands(processor: IA32Processor): String = ""
		override fun handle(processor: IA32Processor) {
			if (processor.flags.getFlag(FlagType.OVERFLOW_FLAG)) processor.initiateInterrupt(0x04u)
		}
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		OneOperand,
		ZeroOperandOverflow
	)
}