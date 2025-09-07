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
class SelectorC1h : InstructionSelector(0xC1u) {
	override val instructions: Map<UInt, Instruction> by lazy {
		mapOf(
			0u to Rotate.Left(
				d16MI8(processor), dc16MI8i(processor),
				d32MI8(processor), dc32MI8i(processor),
			),
			1u to Rotate.Right(
				d16MI8(processor), dc16MI8i(processor),
				d32MI8(processor), dc32MI8i(processor),
			),
			2u to Rotate.LeftWithCarry(
				d16MI8(processor), dc16MI8i(processor),
				d32MI8(processor), dc32MI8i(processor),
			),
			3u to Rotate.RightWithCarry(
				d16MI8(processor), dc16MI8i(processor),
				d32MI8(processor), dc32MI8i(processor),
			),
			4u to Shift.Left(
				d16MI8(processor), dc16MI8i(processor),
				d32MI8(processor), dc32MI8i(processor),
			),
			5u to Shift.Right(
				d16MI8(processor), dc16MI8i(processor),
				d32MI8(processor), dc32MI8i(processor),
			),
			7u to Shift.RightArithmetic(
				d16MI8(processor), dc16MI8i(processor),
				d32MI8(processor), dc32MI8i(processor),
			)
		)
	}
}