package org.bread_experts_group.api.computer.ia32.instruction.impl.group

import org.bread_experts_group.api.computer.ia32.instruction.impl.*
import org.bread_experts_group.api.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.api.computer.ia32.instruction.type.InstructionSelector

/**
 * ALIAS OF SELECTOR 0x80
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class Selector82h : InstructionSelector(0x82u) {
	override val instructions: Map<UInt, Instruction> by lazy {
		mapOf(
			0u to Add.TwoOperand8Bit(0u, d8MI(processor), dc8MI(processor)),
			1u to LogicalOR.TwoOperand8Bit(0u, d8MI(processor), dc8MI(processor)),
			2u to AddWithCarry.TwoOperand8Bit(0u, d8MI(processor), dc8MI(processor)),
			3u to SubtractWithBorrow.TwoOperand8Bit(0u, d8MI(processor), dc8MI(processor)),
			4u to LogicalAND.TwoOperand8Bit(0u, d8MI(processor), dc8MI(processor)),
			5u to Subtract.TwoOperand8Bit(0u, d8MI(processor), dc8MI(processor)),
			6u to LogicalXOR.TwoOperand8Bit(0u, d8MI(processor), dc8MI(processor)),
			7u to SubtractCompare.TwoOperand8Bit(0u, d8MI(processor), dc8MI(processor))
		)
	}
}