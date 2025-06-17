package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.operand.Immediate8

class PushImmediate8OnStack : Instruction(0x6Au, "push"), Immediate8 {
	override fun operands(processor: IA32Processor): String = hex(processor.imm8())
	override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
		AddressingLength.R32 -> processor.push32(processor.imm8().toUInt())
		AddressingLength.R16 -> processor.push16(processor.imm8().toUShort())
		else -> throw UnsupportedOperationException()
	}
}