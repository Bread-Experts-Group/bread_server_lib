package org.bread_experts_group.computer.mos6502.instruction.impl

import org.bread_experts_group.computer.mos6502.MOS6502Processor
import org.bread_experts_group.computer.mos6502.instruction.Instruction
import org.bread_experts_group.hex

object JMPAbsolute : Instruction(0x4Cu, "jmp") {
	override fun handle(processor: MOS6502Processor, disassembly: StringBuilder) {
		val pc = processor.pc
		disassembly.append(" ${hex(pc.value)} -> ")
		pc.value = processor.computer.requestMemoryAt16((pc.value).toULong())
		disassembly.append(hex(pc.value))
		disassembly.append(" (jmp)")
	}
}