package org.bread_experts_group.computer.ia32.instruction.impl.group.hFF

import org.bread_experts_group.computer.ia32.instruction.impl.Add
import org.bread_experts_group.computer.ia32.instruction.impl.Subtract
import org.bread_experts_group.computer.ia32.instruction.impl.group.d16M
import org.bread_experts_group.computer.ia32.instruction.impl.group.d32M
import org.bread_experts_group.computer.ia32.instruction.impl.group.dc16M
import org.bread_experts_group.computer.ia32.instruction.impl.group.dc32M
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.InstructionSelector

/**
 * TODO: Write an encompassing Javadoc for all instructions defined in this selector.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class Selector : InstructionSelector(0xFFu) {
	override fun instructions(): Map<UInt, Instruction> = mapOf(
		0u to Add.TwoOperandIncrement(0u, d16M(processor), dc16M(processor), d32M(processor), dc32M(processor)),
		1u to Subtract.TwoOperandIncrement(0u, d16M(processor), dc16M(processor), d32M(processor), dc32M(processor)),
		2u to NearCallToModRM,
		3u to FarCallToModRM,
		4u to NearJumpToModRM,
		5u to FarJumpToModRM,
		6u to PushModRM
	)
}