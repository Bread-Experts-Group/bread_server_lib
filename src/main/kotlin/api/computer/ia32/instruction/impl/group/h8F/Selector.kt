package org.bread_experts_group.api.computer.ia32.instruction.impl.group.h8F

import org.bread_experts_group.api.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.api.computer.ia32.instruction.type.InstructionSelector

/**
 * TODO: Write an encompassing Javadoc for all instructions defined in this selector.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class Selector : InstructionSelector(0x8Fu) {
	override val instructions: Map<UInt, Instruction> by lazy {
		mapOf(
			0u to PopIntoModRM
		)
	}
}