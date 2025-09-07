package org.bread_experts_group.api.computer.ia32.instruction.impl

import org.bread_experts_group.api.computer.BinaryUtil.hex
import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.api.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.api.computer.ia32.instruction.type.operand.Immediate16

class NearReturnPopStack : Instruction(0xC2u, "retn"), Immediate16 {
	override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
		AddressingLength.R32 -> hex(processor.pop32())
		AddressingLength.R16 -> hex(processor.pop16())
		else -> throw UnsupportedOperationException()
	} + ", ${hex(processor.imm16())}"

	override fun handle(processor: IA32Processor) {
		val pop = processor.imm16()
		processor.ip.tex = when (processor.operandSize) {
			AddressingLength.R32 -> processor.pop32().also { processor.sp.ex += pop }
			AddressingLength.R16 -> processor.pop16().toUInt().also { processor.sp.x += pop }
			else -> throw UnsupportedOperationException()
		}
	}
}