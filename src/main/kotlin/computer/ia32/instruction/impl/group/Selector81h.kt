package org.bread_experts_group.computer.ia32.instruction.impl.group

import org.bread_experts_group.computer.ia32.instruction.impl.*
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.InstructionSelector

/**
 * TODO: Write an encompassing Javadoc for all instructions defined in this selector.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class Selector81h : InstructionSelector(0x81u) {
	override val instructions: Map<UInt, Instruction> by lazy {
		mapOf(
			0u to Add.TwoOperand(0u, d16MI(processor), dc16MI(processor), d32MI(processor), dc32MI(processor)),
			1u to LogicalOR.TwoOperand(0u, d16MI(processor), dc16MI(processor), d32MI(processor), dc32MI(processor)),
			2u to AddWithCarry.TwoOperand(0u, d16MI(processor), dc16MI(processor), d32MI(processor), dc32MI(processor)),
			3u to SubtractWithBorrow.TwoOperand(
				0u,
				d16MI(processor), dc16MI(processor),
				d32MI(processor), dc32MI(processor)
			),
			4u to LogicalAND.TwoOperand(0u, d16MI(processor), dc16MI(processor), d32MI(processor), dc32MI(processor)),
			5u to Subtract.TwoOperand(0u, d16MI(processor), dc16MI(processor), d32MI(processor), dc32MI(processor)),
			6u to LogicalXOR.TwoOperand(0u, d16MI(processor), dc16MI(processor), d32MI(processor), dc32MI(processor)),
			7u to SubtractCompare.TwoOperand(
				0u,
				d16MI(processor), dc16MI(processor),
				d32MI(processor), dc32MI(processor)
			)
		)
	}
}