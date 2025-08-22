package org.bread_experts_group.computer.mos6502.instruction.impl

import org.bread_experts_group.computer.mos6502.MOS6502Processor
import org.bread_experts_group.computer.mos6502.instruction.Instruction
import org.bread_experts_group.computer.mos6502.register.StatusRegister
import org.bread_experts_group.hex

object CompareMemoryWithAccumulator : Instruction(0xC9u, "cmp") {
	override fun handle(processor: MOS6502Processor, disassembly: StringBuilder) {
		val addr = currentAddr(processor)
		val memory = processor.computer.getMemoryAt(addr.toULong())
		val result = processor.a.value - memory

		disassembly.append(" result [$memory/${hex(memory)}]")

		processor.status.setFlag(StatusRegister.FlagType.ZERO, result.toUByte() == memory)
		disassembly.append(" | ZERO -> ${result.toUByte() == memory} ")

		processor.status.setFlag(StatusRegister.FlagType.NEGATIVE, shr7equals1(result.toUByte()))
		disassembly.append("| NEGATIVE -> ${shr7equals1(result.toUByte())} ")

		processor.status.setFlag(StatusRegister.FlagType.CARRY, memory <= result)
		disassembly.append("| CARRY -> ${memory <= result}")

		processor.pc.value++
		disassembly.append(" (cmp)")
	}
}