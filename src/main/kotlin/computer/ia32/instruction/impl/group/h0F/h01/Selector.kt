package org.bread_experts_group.computer.ia32.instruction.impl.group.h0F.h01

import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.InstructionSelector

/**
 * TODO: Write an encompassing Javadoc for all instructions defined in this selector.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class Selector : InstructionSelector(0x0F01u) {
	override fun instructions(): Map<UInt, Instruction> = mapOf(
		0u to SaveDescriptorTableLocation('g', processor.gdtrBase, processor.gdtrLimit),
		1u to SaveDescriptorTableLocation('i', processor.idtrBase, processor.idtrLimit),
		2u to LoadDescriptorTableLocation('g', processor.gdtrBase, processor.gdtrLimit),
		3u to LoadDescriptorTableLocation('i', processor.idtrBase, processor.idtrLimit),
	)
}