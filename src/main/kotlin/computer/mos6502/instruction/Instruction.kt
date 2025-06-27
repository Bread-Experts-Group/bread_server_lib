package org.bread_experts_group.computer.mos6502.instruction

import org.bread_experts_group.computer.BinaryUtil.shr
import org.bread_experts_group.computer.mos6502.MOS6502Processor
import org.bread_experts_group.hex

abstract class Instruction(val opcode: UInt, val mnemonic: String) {
	abstract fun handle(processor: MOS6502Processor, disassembly: StringBuilder)
	override fun toString(): String = "Instruction[${hex(opcode)} : $mnemonic]"

	fun shr7equals1(byte: UByte): Boolean = ((byte shr 7) and 1u).toUInt() == 1u
	fun valueIs0(byte: UByte): Boolean = byte.toUInt() == 0u
	fun currentAddr(processor: MOS6502Processor): UShort = (processor.pc.value - 1u).toUShort()
}