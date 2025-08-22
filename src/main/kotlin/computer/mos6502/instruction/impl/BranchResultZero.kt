package org.bread_experts_group.computer.mos6502.instruction.impl

import org.bread_experts_group.computer.mos6502.MOS6502Processor
import org.bread_experts_group.computer.mos6502.instruction.Instruction
import org.bread_experts_group.computer.mos6502.register.StatusRegister
import org.bread_experts_group.hex

object BranchResultZero : Instruction(0xF0u, "beq") {
	override fun handle(processor: MOS6502Processor, disassembly: StringBuilder) {
		val flag = processor.status.getFlag(StatusRegister.FlagType.ZERO)
		val pc = processor.pc
		if (flag) {
			disassembly.append(" [${hex(currentAddr(processor))} -> ")
			pc.value =
				((pc.value.toInt() + 2) + processor.computer.getMemoryAt(pc.value.toULong()).toByte()).toUShort()
			disassembly.append(" ${hex(currentAddr(processor))}")
		}
		disassembly.append(" (beq)")
	}
}