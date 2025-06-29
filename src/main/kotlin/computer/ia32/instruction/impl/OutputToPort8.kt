package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.operand.Immediate8

class OutputToPort8 : Instruction(0xE6u, "out"), Immediate8 {
	override fun operands(processor: IA32Processor): String = "${hex(processor.imm8())}, al"
	override fun handle(processor: IA32Processor) {
		processor.computer.ioMap.getValue(processor.imm8().toUInt()).write(processor.a.tl)
	}
}