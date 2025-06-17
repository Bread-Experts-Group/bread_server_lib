package org.bread_experts_group.computer.ia32.instruction.impl.intr

import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.operand.Immediate8

class FireSoftwareInterrupt : Instruction(0xCDu, "int"), Immediate8 {
	override fun operands(processor: IA32Processor): String = hex(processor.imm8())
	override fun handle(processor: IA32Processor): Unit = processor.initiateInterrupt(processor.imm8())
}