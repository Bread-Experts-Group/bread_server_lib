package org.bread_experts_group.api.computer.mos6502.instruction.impl

import org.bread_experts_group.api.computer.mos6502.MOS6502Processor
import org.bread_experts_group.api.computer.mos6502.instruction.Instruction
import org.bread_experts_group.hex

object StoreAccumulatorInMemory : Instruction(0x8Du, "sta") {
	override fun handle(processor: MOS6502Processor, disassembly: StringBuilder) {
		val memoryAddr = currentAddr(processor)
		processor.computer.setMemoryAt(memoryAddr.toULong(), processor.a.value)
		disassembly.append(" a [${processor.a.value}/${hex(processor.a.value)}] -> ${hex(memoryAddr)} |")
		disassembly.append(" (sta)")
		processor.pc.value++
		processor.pc.value++
	}
}