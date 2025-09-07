package org.bread_experts_group.api.computer.mos6502.instruction.impl

import org.bread_experts_group.api.computer.mos6502.MOS6502Processor
import org.bread_experts_group.api.computer.mos6502.instruction.Instruction
import org.bread_experts_group.hex

object TransferIndexXToStackPointer : Instruction(0x9Au, "txs") {
	override fun handle(processor: MOS6502Processor, disassembly: StringBuilder) {
		processor.s.value = processor.x.value

		disassembly.append(" ${processor.x.value}/${hex(processor.x.value)} -> s")
		disassembly.append(" (txs)")
	}
}