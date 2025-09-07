package org.bread_experts_group.api.computer.ia32.instruction.impl

import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.api.computer.ia32.register.FlagsRegister.FlagType

class SetCarryFlagToAL : Instruction(0xD6u, "salc") {
	override fun operands(processor: IA32Processor): String = ""
	override fun handle(processor: IA32Processor) {
		processor.a.l =
			if (processor.flags.getFlag(FlagType.CARRY_FLAG)) 0xFFu
			else 0x00u
	}
}