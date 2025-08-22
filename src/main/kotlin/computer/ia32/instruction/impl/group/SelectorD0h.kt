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
class SelectorD0h : InstructionSelector(0xD0u) {
	override val instructions: Map<UInt, Instruction> by lazy {
		mapOf(
			0u to Rotate.Left8Bit(d8M1(processor), dc8M1(processor)),
			1u to Rotate.Right8Bit(d8M1(processor), dc8M1(processor)),
			2u to Rotate.LeftWithCarry8Bit(d8M1(processor), dc8M1(processor)),
			3u to Rotate.RightWithCarry8Bit(d8M1(processor), dc8M1(processor)),
			4u to Shift.Left8Bit(d8M1(processor), dc8M1(processor)),
			5u to Shift.Right8Bit(d8M1(processor), dc8M1(processor)),
			7u to Shift.RightArithmetic8Bit(d8M1(processor), dc8M1(processor))
		)
	}
}