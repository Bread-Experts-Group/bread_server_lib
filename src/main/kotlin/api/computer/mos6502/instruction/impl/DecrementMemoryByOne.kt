package org.bread_experts_group.api.computer.mos6502.instruction.impl

import org.bread_experts_group.api.computer.mos6502.MOS6502Processor
import org.bread_experts_group.api.computer.mos6502.instruction.Instruction
import org.bread_experts_group.hex

object DecrementMemoryByOne : Instruction(0xDEu, "dec") {
	override fun handle(processor: MOS6502Processor, disassembly: StringBuilder) {
		val addr = currentAddr(processor) + processor.x.value
		val memory = processor.computer.getMemoryAt(addr.toULong())
		val dec = (memory - 1u).toUByte()
		disassembly.append(" [$memory/${hex(memory)}] -> ")
		processor.computer.setMemoryAt(addr.toULong(), dec)
		processor.pc.value++
		disassembly.append("$dec/${hex(dec)}")
		disassembly.append(" (dec)")
	}
}