package org.bread_experts_group.computer.mos6502.instruction.impl

import org.bread_experts_group.computer.mos6502.MOS6502Processor
import org.bread_experts_group.computer.mos6502.instruction.Instruction
import org.bread_experts_group.computer.mos6502.register.StatusRegister
import org.bread_experts_group.hex

object DecrementX : Instruction(0xCAu, "dex") {
	override fun handle(processor: MOS6502Processor, disassembly: StringBuilder) {
		disassembly.append(" x [${processor.x.value}/${hex(processor.x.value)}] ->")
		processor.x.value--
		disassembly.append(" ${processor.x.value}/${hex(processor.x.value)}")

		processor.status.setFlag(StatusRegister.FlagType.ZERO, valueIs0(processor.x.value))
		disassembly.append(" | ZERO -> ${valueIs0(processor.x.value)} ")

		processor.status.setFlag(StatusRegister.FlagType.NEGATIVE, shr7equals1(processor.x.value))
		disassembly.append("| NEGATIVE -> ${shr7equals1(processor.x.value)} |")

		disassembly.append(" (dex)")
	}
}