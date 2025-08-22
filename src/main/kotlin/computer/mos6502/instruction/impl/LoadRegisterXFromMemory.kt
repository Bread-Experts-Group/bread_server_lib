package org.bread_experts_group.computer.mos6502.instruction.impl

import org.bread_experts_group.computer.mos6502.MOS6502Processor
import org.bread_experts_group.computer.mos6502.instruction.Instruction
import org.bread_experts_group.computer.mos6502.register.StatusRegister
import org.bread_experts_group.hex

object LoadRegisterXFromMemory : Instruction(0xA2u, "ldx") {
	override fun handle(processor: MOS6502Processor, disassembly: StringBuilder) {
		val addr = processor.computer.getMemoryAt(currentAddr(processor).toULong())
		processor.x.value = addr
		disassembly.append(" $addr/${hex(addr)} -> x")
		processor.status.setFlag(StatusRegister.FlagType.ZERO, valueIs0(processor.x.value))
		disassembly.append(" | ZERO -> ${valueIs0(processor.x.value)} ")
		processor.status.setFlag(StatusRegister.FlagType.NEGATIVE, shr7equals1(processor.x.value))
		disassembly.append("| NEGATIVE -> ${shr7equals1(processor.x.value)} |")
		processor.pc.value++
		disassembly.append(" (ldx)")
	}
}