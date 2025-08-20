package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.type.Instruction

class Push : InstructionCluster {
	class OneOperand8Bit(
		opcode: UInt,
		val d: () -> String,
		val dc: () -> UByte
	) : Instruction(opcode, "push") {
		override fun operands(processor: IA32Processor): String = d()
		override fun handle(processor: IA32Processor) {
			when (processor.operandSize) {
				AddressingLength.R32 -> processor.push32(dc().toByte().toInt().toUInt())
				AddressingLength.R16 -> processor.push16(dc().toByte().toShort().toUShort())
				else -> throw UnsupportedOperationException()
			}
		}
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
	)
}