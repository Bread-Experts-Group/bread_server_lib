package org.bread_experts_group.computer.ia32.instruction.impl.group.hFF

import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.InstructionSelector

/**
 * TODO: Write an encompassing Javadoc for all instructions defined in this selector.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class Selector : InstructionSelector(0xFFu) {
	override fun instructions(): Map<UInt, Instruction> = mapOf(
		0u to IncrementModRM,
		4u to NearJumpToModRM,
		6u to PushModRM
	)
}