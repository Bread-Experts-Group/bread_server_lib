package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.flag.LogicalArithmeticFlagOperations
import org.bread_experts_group.computer.ia32.instruction.type.operand.Immediate8

class ANDImmediate8WithAL : Instruction(0x24u, "and"), Immediate8, LogicalArithmeticFlagOperations {
	override fun operands(processor: IA32Processor): String = "al, ${hex(processor.imm8())}"
	override fun handle(processor: IA32Processor) {
		val result = processor.a.tl and processor.imm8()
		this.setFlagsForResult(processor, result)
		processor.a.tl = result
	}
}