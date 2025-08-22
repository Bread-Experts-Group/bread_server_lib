package org.bread_experts_group.computer.mos6502.instruction.impl

import org.bread_experts_group.computer.mos6502.MOS6502Processor
import org.bread_experts_group.computer.mos6502.instruction.Instruction
import org.bread_experts_group.hex

object IncrementMemoryByOne : Instruction(0xFEu, "inc") {
	override fun handle(processor: MOS6502Processor, disassembly: StringBuilder) {
		val addr = currentAddr(processor) + processor.x.value
		val memory = processor.computer.getMemoryAt(addr.toULong())
		val inc = (memory + 1u).toUByte()
		disassembly.append(" [$memory/${hex(memory)}] -> ")
		processor.computer.setMemoryAt(addr.toULong(), inc)
		disassembly.append("$inc/${hex(inc)}")
		disassembly.append(" (inc)")
	}
}