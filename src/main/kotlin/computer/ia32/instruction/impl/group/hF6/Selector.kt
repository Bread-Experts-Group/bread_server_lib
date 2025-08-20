package org.bread_experts_group.computer.ia32.instruction.impl.group.hF6

import org.bread_experts_group.computer.ia32.instruction.impl.LogicalCompare
import org.bread_experts_group.computer.ia32.instruction.impl.OnesComplementNegation
import org.bread_experts_group.computer.ia32.instruction.impl.TwosComplementNegation
import org.bread_experts_group.computer.ia32.instruction.impl.group.d8M
import org.bread_experts_group.computer.ia32.instruction.impl.group.d8MI
import org.bread_experts_group.computer.ia32.instruction.impl.group.dc8M
import org.bread_experts_group.computer.ia32.instruction.impl.group.dc8MI
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.InstructionSelector

/**
 * TODO: Write an encompassing Javadoc for all instructions defined in this selector.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class Selector : InstructionSelector(0xF6u) {
	override fun instructions(): Map<UInt, Instruction> = mapOf(
		0u to LogicalCompare.TwoOperand8Bit(0u, d8MI(processor), dc8MI(processor)),
		2u to OnesComplementNegation.SingleOperand8Bit(0u, d8M(processor), dc8M(processor)),
		3u to TwosComplementNegation.SingleOperand8Bit(0u, d8M(processor), dc8M(processor)),
		4u to MultiplyALByModRM8,
		5u to SignedMultiplyALByModRM8,
		6u to DivideAXByModRM8
	)
}