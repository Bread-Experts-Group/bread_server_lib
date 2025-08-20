package org.bread_experts_group.computer.ia32.instruction.impl.group

import org.bread_experts_group.computer.ia32.instruction.impl.Rotate
import org.bread_experts_group.computer.ia32.instruction.impl.Shift
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.InstructionSelector

/**
 * TODO: Write an encompassing Javadoc for all instructions defined in this selector.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class SelectorD3h : InstructionSelector(0xD3u) {
	override fun instructions(): Map<UInt, Instruction> = mapOf(
		0u to Rotate.Left(
			d16MC(processor), dc16MCi(processor),
			d32MC(processor), dc32MCi(processor)
		),
		1u to Rotate.Right(
			d16MC(processor), dc16MCi(processor),
			d32MC(processor), dc32MCi(processor)
		),
		2u to Rotate.LeftWithCarry(
			d16MC(processor), dc16MCi(processor),
			d32MC(processor), dc32MCi(processor)
		),
		3u to Rotate.RightWithCarry(
			d16MC(processor), dc16MCi(processor),
			d32MC(processor), dc32MCi(processor)
		),
		4u to Shift.Left(
			d16MC(processor), dc16MCi(processor),
			d32MC(processor), dc32MCi(processor)
		),
		5u to Shift.Right(
			d16MC(processor), dc16MCi(processor),
			d32MC(processor), dc32MCi(processor)
		),
		7u to Shift.RightArithmetic(
			d16MC(processor), dc16MCi(processor),
			d32MC(processor), dc32MCi(processor)
		)
	)
}