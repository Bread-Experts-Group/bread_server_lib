package org.bread_experts_group.api.computer.ia32.instruction.impl

import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.api.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.api.computer.ia32.register.FlagsRegister.FlagType

class SpecificFlagModificationDefinitions : InstructionCluster {
	class SpecificFlagModification(
		opcode: UInt,
		n: Char,
		val flag: FlagType,
		val state: Boolean
	) : Instruction(opcode, "${if (state) "st" else "cl"}$n") {
		override fun operands(processor: IA32Processor): String = ""
		override fun handle(processor: IA32Processor) {
			processor.flags.setFlag(this.flag, this.state)
		}
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		SpecificFlagModification(0xF8u, 'c', FlagType.CARRY_FLAG, false),
		SpecificFlagModification(0xF9u, 'c', FlagType.CARRY_FLAG, true),
		SpecificFlagModification(0xFAu, 'i', FlagType.INTERRUPT_ENABLE_FLAG, false),
		SpecificFlagModification(0xFBu, 'i', FlagType.INTERRUPT_ENABLE_FLAG, true),
		SpecificFlagModification(0xFCu, 'd', FlagType.DIRECTION_FLAG, false),
		SpecificFlagModification(0xFDu, 'd', FlagType.DIRECTION_FLAG, true)
	)
}