package org.bread_experts_group.api.computer.ia32.instruction.impl.group

import org.bread_experts_group.api.computer.ia32.instruction.impl.Rotate
import org.bread_experts_group.api.computer.ia32.instruction.impl.Shift
import org.bread_experts_group.api.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.api.computer.ia32.instruction.type.InstructionSelector

/**
 * TODO: Write an encompassing Javadoc for all instructions defined in this selector.
 * @author Miko Elbrecht
 * @since D0F0N0P0
 */
class SelectorC0h : InstructionSelector(0xC0u) {
	override val instructions: Map<UInt, Instruction> by lazy {
		mapOf(
			0u to Rotate.Left8Bit(d8MI8(processor), dc8MI8i(processor)),
			1u to Rotate.Right8Bit(d8MI8(processor), dc8MI8i(processor)),
			2u to Rotate.Left8Bit(d8MI8(processor), dc8MI8i(processor)),
			3u to Rotate.Right8Bit(d8MI8(processor), dc8MI8i(processor)),
			4u to Shift.Left8Bit(d8MI8(processor), dc8MI8i(processor)),
			5u to Shift.Right8Bit(d8MI8(processor), dc8MI8i(processor)),
			7u to Shift.RightArithmetic8Bit(d8MI8(processor), dc8MI8i(processor))
		)
	}
}