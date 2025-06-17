package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.flag.ArithmeticSubtractionFlagOperations
import org.bread_experts_group.computer.ia32.instruction.type.operand.Immediate16
import org.bread_experts_group.computer.ia32.instruction.type.operand.Immediate32

class SubtractImmediateFromA : Instruction(0x2Du, "sub"), Immediate32, Immediate16,
	ArithmeticSubtractionFlagOperations {
	override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
		AddressingLength.R32 -> "eax, ${hex(processor.imm32())}"
		AddressingLength.R16 -> "ax, ${hex(processor.imm16())}"
		else -> throw UnsupportedOperationException()
	}

	override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
		AddressingLength.R32 -> {
			val result = this.setFlagsForOperationR(processor, processor.a.tex, processor.imm32())
			processor.a.tex = result
			this.setFlagsForResult(processor, result)
		}

		AddressingLength.R16 -> {
			val result = this.setFlagsForOperationR(processor, processor.a.tx, processor.imm16())
			processor.a.tx = result
			this.setFlagsForResult(processor, result)
		}

		else -> throw UnsupportedOperationException()
	}
}