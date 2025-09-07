package org.bread_experts_group.api.computer.mos6502.instruction.impl

import org.bread_experts_group.api.computer.mos6502.MOS6502Processor
import org.bread_experts_group.api.computer.mos6502.instruction.Instruction
import org.bread_experts_group.api.computer.mos6502.register.StatusRegister

object ClearDecimalMode : Instruction(0xD8u, "cld") {
	override fun handle(processor: MOS6502Processor, disassembly: StringBuilder) {
		processor.status.setFlag(StatusRegister.FlagType.DECIMAL, false)

		disassembly.append(" (cld)")
	}
}