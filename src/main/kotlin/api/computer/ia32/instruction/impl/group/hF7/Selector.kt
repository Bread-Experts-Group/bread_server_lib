package org.bread_experts_group.api.computer.ia32.instruction.impl.group.hF7

import org.bread_experts_group.api.computer.ia32.instruction.impl.LogicalCompare
import org.bread_experts_group.api.computer.ia32.instruction.impl.OnesComplementNegation
import org.bread_experts_group.api.computer.ia32.instruction.impl.TwosComplementNegation
import org.bread_experts_group.api.computer.ia32.instruction.impl.group.*
import org.bread_experts_group.api.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.api.computer.ia32.instruction.type.InstructionSelector

/**
 * TODO: Write an encompassing Javadoc for all instructions defined in this selector.
 * @author Miko Elbrecht
 * @since D0F0N0P0
 */
class Selector : InstructionSelector(0xF7u) {
	override val instructions: Map<UInt, Instruction> by lazy {
		mapOf(
			0u to LogicalCompare.TwoOperand(
				0u,
				d16MI(processor), dc16MI(processor),
				d32MI(processor), dc32MI(processor)
			),
			2u to OnesComplementNegation.SingleOperand(
				0u,
				d16M(processor), dc16M(processor),
				d32M(processor), dc32M(processor)
			),
			3u to TwosComplementNegation.SingleOperand(
				0u,
				d16M(processor), dc16M(processor),
				d32M(processor), dc32M(processor)
			),
			4u to MultiplyModRM,
			6u to DivideModRM
		)
	}
}