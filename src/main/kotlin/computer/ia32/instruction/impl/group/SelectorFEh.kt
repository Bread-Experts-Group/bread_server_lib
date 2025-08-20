package org.bread_experts_group.computer.ia32.instruction.impl.group

import org.bread_experts_group.computer.ia32.instruction.impl.Add
import org.bread_experts_group.computer.ia32.instruction.impl.Subtract
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.InstructionSelector

/**
 * TODO: Write an encompassing Javadoc for all instructions defined in this selector.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class SelectorFEh : InstructionSelector(0xFEu) {
	override fun instructions(): Map<UInt, Instruction> = mapOf(
		0u to Add.TwoOperand8BitIncrement(0u, d8M(processor), dc8M(processor)),
		1u to Subtract.TwoOperand8BitIncrement(0u, d8M(processor), dc8M(processor))
	)
}