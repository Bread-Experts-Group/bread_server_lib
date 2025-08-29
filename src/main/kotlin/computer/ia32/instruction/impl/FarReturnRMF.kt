package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.imm16
import org.bread_experts_group.hex

class FarReturnRMF : Instruction(0xCAu, "retf") {
	override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
		AddressingLength.R16 -> {
			val ip = processor.pop16()
			val cs = processor.pop16()
			"${hex(cs)}:${ip.toHexString(HexFormat.UpperCase)}"
		}

		else -> throw UnsupportedOperationException()
	}

	override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
		AddressingLength.R16 -> {
			processor.ip.tex = processor.pop16().toUInt()
			processor.cs.tx = processor.pop16()
			processor.sp.x += processor.imm16()
		}

		else -> throw UnsupportedOperationException()
	}
}