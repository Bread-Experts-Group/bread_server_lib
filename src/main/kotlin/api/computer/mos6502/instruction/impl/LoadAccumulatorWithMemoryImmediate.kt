package org.bread_experts_group.api.computer.mos6502.instruction.impl

import org.bread_experts_group.api.computer.mos6502.MOS6502Processor
import org.bread_experts_group.api.computer.mos6502.instruction.Instruction
import org.bread_experts_group.api.computer.mos6502.register.StatusRegister
import org.bread_experts_group.hex

object LoadAccumulatorWithMemoryImmediate : Instruction(0xA9u, "lda") {
	override fun handle(processor: MOS6502Processor, disassembly: StringBuilder) {
		val addr = processor.computer.getMemoryAt(currentAddr(processor).toULong())
		processor.a.value = addr
		disassembly.append(" $addr/${hex(addr)} -> a")

		processor.status.setFlag(StatusRegister.FlagType.ZERO, valueIs0(processor.a.value))
		disassembly.append(" | ZERO -> ${valueIs0(processor.a.value)} ")

		processor.status.setFlag(StatusRegister.FlagType.NEGATIVE, shr7equals1(processor.a.value))
		disassembly.append("| NEGATIVE -> ${shr7equals1(processor.a.value)} |")

		processor.pc.value++
		disassembly.append(" (lda)")
	}
}