package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.type.Instruction

class NearReturn : Instruction(0xC3u, "ret") {
	override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
		AddressingLength.R32 -> hex(processor.pop32())
		AddressingLength.R16 -> hex(processor.pop16())
		else -> throw UnsupportedOperationException()
	}

	override fun handle(processor: IA32Processor) {
		processor.ip.tex = when (processor.operandSize) {
			AddressingLength.R32 -> processor.pop32()
			AddressingLength.R16 -> processor.pop16().toUInt()
			else -> throw UnsupportedOperationException()
		}
	}
}