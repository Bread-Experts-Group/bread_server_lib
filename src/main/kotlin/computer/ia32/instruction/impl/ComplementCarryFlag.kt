package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.register.FlagsRegister.FlagType

class ComplementCarryFlag : Instruction(0xF5u, "cmc") {
	override fun operands(processor: IA32Processor): String = ""
	override fun handle(processor: IA32Processor) {
		processor.flags.setFlag(FlagType.CARRY_FLAG, !processor.flags.getFlag(FlagType.CARRY_FLAG))
	}
}