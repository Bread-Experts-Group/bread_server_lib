package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.flag.LogicalArithmeticFlagOperations
import org.bread_experts_group.computer.ia32.instruction.type.operand.Immediate8

/**
 * Opcode: `0C ib` |
 * Instruction: `OR AL, imm8` |
 * Flags Modified: `OF, CR` (clr) / `SF, ZF, PF` (result dep)
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class RegisterALImmediate8OR : Instruction(0x0Cu, "or"), Immediate8, LogicalArithmeticFlagOperations {
	override fun operands(processor: IA32Processor): String = "al, ${hex(processor.imm8())}"
	override fun handle(processor: IA32Processor) {
		val result = processor.a.tl or processor.imm8()
		processor.a.tl = result
		this.setFlagsForResult(processor, result)
	}
}