package org.bread_experts_group.computer.ia32.instruction.impl.group.hD3

import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.InstructionSelector

/**
 * TODO: Write an encompassing Javadoc for all instructions defined in this selector.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class Selector : InstructionSelector(0xD3u) {
	override fun instructions(): Map<UInt, Instruction> = mapOf(
		4u to ShiftModRMLeftCL,
		5u to ShiftModRMRightCL
	)
}