package org.bread_experts_group.api.computer.ia32.instruction.impl

import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.api.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.api.computer.ia32.instruction.type.Instruction

class Convert : InstructionCluster {
	object ConvertA : Instruction(0x99u, "c") {
		override fun operands(processor: IA32Processor): String = ""
		override fun handle(processor: IA32Processor) {
			when (processor.operandSize) {
				AddressingLength.R32 -> {
					val extended = processor.a.tex.toInt().toLong()
					processor.d.tex = (extended ushr 32).toUInt()
				}

				AddressingLength.R16 -> {
					val extended = processor.a.tx.toShort().toInt()
					processor.d.tx = (extended ushr 16).toUShort()
				}

				else -> throw UnsupportedOperationException()
			}
		}
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		ConvertA
	)
}