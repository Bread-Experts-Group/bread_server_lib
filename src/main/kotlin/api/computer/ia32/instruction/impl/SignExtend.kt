package org.bread_experts_group.api.computer.ia32.instruction.impl

import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.api.computer.ia32.instruction.type.Instruction

class SignExtend : Instruction(0x98u, "cbw/de") {
	override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
		AddressingLength.R32 -> "eax"
		AddressingLength.R16 -> "ax"
		else -> throw UnsupportedOperationException()
	}

	override fun handle(processor: IA32Processor) {
		when (processor.operandSize) {
			AddressingLength.R16 -> processor.a.tx = processor.a.l.toByte().toUShort()
			else -> throw UnsupportedOperationException()
		}
	}
}