package org.bread_experts_group.computer.mos6502.instruction.impl

import org.bread_experts_group.computer.mos6502.MOS6502Processor
import org.bread_experts_group.computer.mos6502.instruction.Instruction
import org.bread_experts_group.computer.mos6502.register.StatusRegister
import org.bread_experts_group.hex

object BranchResultNotZero : Instruction(0xD0u, "bne") {
	override fun handle(processor: MOS6502Processor, disassembly: StringBuilder) {
		val flag = processor.status.getFlag(StatusRegister.FlagType.ZERO)
		val pc = processor.pc
		if (!flag) {
			disassembly.append(" ${hex(processor.pc.value)} -> ")
			pc.value =
				((pc.value.toInt() + 2) + processor.computer.requestMemoryAt(pc.value.toULong()).toByte()).toUShort()
			disassembly.append(hex(processor.pc.value))
		}
		disassembly.append(" (bne)")
	}
}