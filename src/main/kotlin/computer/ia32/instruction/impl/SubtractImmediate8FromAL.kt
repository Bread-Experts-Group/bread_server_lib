package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.flag.ArithmeticSubtractionFlagOperations
import org.bread_experts_group.computer.ia32.instruction.type.operand.Immediate8

class SubtractImmediate8FromAL : Instruction(0x2Cu, "sub"), Immediate8, ArithmeticSubtractionFlagOperations {
	override fun operands(processor: IA32Processor): String = "al, ${hex(processor.imm8())}"
	override fun handle(processor: IA32Processor) {
		val result = this.setFlagsForOperationR(processor, processor.a.tl, processor.imm8())
		processor.a.tl = result
		this.setFlagsForResult(processor, result)
	}
}