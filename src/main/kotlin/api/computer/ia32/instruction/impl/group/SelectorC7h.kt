package org.bread_experts_group.api.computer.ia32.instruction.impl.group

import org.bread_experts_group.api.computer.ia32.instruction.impl.Move
import org.bread_experts_group.api.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.api.computer.ia32.instruction.type.InstructionSelector

/**
 * TODO: Write an encompassing Javadoc for all instructions defined in this selector.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class SelectorC7h : InstructionSelector(0xC7u) {
	override val instructions: Map<UInt, Instruction> by lazy {
		mapOf(
			0u to Move.TwoOperand(
				0u,
				d16MI(processor), dc16MI(processor),
				d32MI(processor), dc32MI(processor)
			)
		)
	}
}