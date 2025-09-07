package org.bread_experts_group.api.computer.ia32.instruction.impl.group

import org.bread_experts_group.api.computer.ia32.instruction.impl.Rotate
import org.bread_experts_group.api.computer.ia32.instruction.impl.Shift
import org.bread_experts_group.api.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.api.computer.ia32.instruction.type.InstructionSelector

/**
 * TODO: Write an encompassing Javadoc for all instructions defined in this selector.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class SelectorD1h : InstructionSelector(0xD1u) {
	override val instructions: Map<UInt, Instruction> by lazy {
		mapOf(
			0u to Rotate.Left(
				d16M1(processor), dc16M1(processor),
				d32M1(processor), dc32M1((processor))
			),
			1u to Rotate.Right(
				d16M1(processor), dc16M1(processor),
				d32M1(processor), dc32M1((processor))
			),
			2u to Rotate.LeftWithCarry(
				d16M1(processor), dc16M1(processor),
				d32M1(processor), dc32M1((processor))
			),
			3u to Rotate.RightWithCarry(
				d16M1(processor), dc16M1(processor),
				d32M1(processor), dc32M1((processor))
			),
			4u to Shift.Left(
				d16M1(processor), dc16M1(processor),
				d32M1(processor), dc32M1((processor))
			),
			5u to Shift.Right(
				d16M1(processor), dc16M1(processor),
				d32M1(processor), dc32M1((processor))
			),
			7u to Shift.RightArithmetic(
				d16M1(processor), dc16M1(processor),
				d32M1(processor), dc32M1((processor))
			)
		)
	}
}