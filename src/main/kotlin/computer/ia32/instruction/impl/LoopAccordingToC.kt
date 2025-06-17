package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.operand.Immediate8

class LoopAccordingToC : Instruction(0xE2u, "loop"), Immediate8 {
	override fun operands(processor: IA32Processor): String = processor.rel8().let {
		"${hex(it)} [${hex((processor.ip.tex.toInt() + it).toUInt())}]" + when (processor.operandSize) {
			AddressingLength.R32 -> " [ecx ${hex(processor.c.tex)}]"
			AddressingLength.R16 -> " [cx ${hex(processor.c.tx)}]"
			else -> throw UnsupportedOperationException()
		}
	}

	override fun handle(processor: IA32Processor) {
		processor.c.ex--
		val r8 = processor.rel8()
		when (processor.operandSize) {
			AddressingLength.R32 -> if (processor.c.ex > 0u) processor.ip.tex = (processor.ip.tex.toInt() + r8).toUInt()
			AddressingLength.R16 -> if (processor.c.x > 0u) processor.ip.tex =
				(processor.ip.tex.toInt() + r8).toUShort().toUInt()

			else -> throw UnsupportedOperationException()
		}
	}
}