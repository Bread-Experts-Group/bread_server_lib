package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.operand.Immediate16
import org.bread_experts_group.computer.ia32.instruction.type.operand.Immediate32

class MoveSegmentOffsetToA8 : Instruction(0xA0u, "mov"), Immediate32, Immediate16 {
	override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
		AddressingLength.R32 -> "al, [${hex(processor.segment.offset(processor.imm32().toULong()))}]"
		AddressingLength.R16 -> "al, [${hex(processor.segment.offset(processor.imm16().toULong()))}]"
		else -> throw UnsupportedOperationException()
	}

	override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
		AddressingLength.R32 -> {
			processor.a.tl = processor.computer.requestMemoryAt(
				processor.segment.offset(processor.imm32().toULong())
			)
		}

		AddressingLength.R16 -> {
			processor.a.tl = processor.computer.requestMemoryAt(
				processor.segment.offset(processor.imm16().toULong())
			)
		}

		else -> throw UnsupportedOperationException()
	}
}