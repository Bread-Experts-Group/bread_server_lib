package org.bread_experts_group.computer.mos6502.instruction.impl

import org.bread_experts_group.computer.mos6502.MOS6502Processor
import org.bread_experts_group.computer.mos6502.instruction.Instruction
import org.bread_experts_group.computer.mos6502.register.StatusRegister

object Break : Instruction(0x00u, "int") {
	override fun handle(processor: MOS6502Processor, disassembly: StringBuilder) {
		if (!processor.status.getFlag(StatusRegister.FlagType.INTERRUPT)) TODO("${processor.pc.value} : BRK")
		else disassembly.append(" (int)")
	}
}