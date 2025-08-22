package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.type.Instruction

class ScanString : InstructionCluster {
	object ZeroOperand8Bit : Instruction(0xAEu, "scas") {
		override fun operands(processor: IA32Processor): String = ""
		override fun handle(processor: IA32Processor) {
			setFlagsSFZFPF8(
				processor,
				subtractAndSetFlagsAFCFOF8(
					processor,
					processor.a.tl,
					processor.computer.getMemoryAt(esDIOffset(processor))
				)
			)
			moveDI(processor, 1u)
		}
	}

	object ZeroOperand : Instruction(0xAFu, "scas") {
		override fun operands(processor: IA32Processor): String = ""
		override fun handle(processor: IA32Processor) {
			when (processor.operandSize) {
				AddressingLength.R32 -> {
					setFlagsSFZFPF32(
						processor,
						subtractAndSetFlagsAFCFOF32(
							processor,
							processor.a.tex,
							processor.computer.getMemoryAt32(esDIOffset(processor))
						)
					)
					moveDI(processor, 4u)
				}

				AddressingLength.R16 -> {
					setFlagsSFZFPF16(
						processor,
						subtractAndSetFlagsAFCFOF16(
							processor,
							processor.a.tx,
							processor.computer.getMemoryAt16(esDIOffset(processor))
						)
					)
					moveDI(processor, 2u)
				}

				else -> throw UnsupportedOperationException()
			}
		}
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		ZeroOperand8Bit,
		ZeroOperand
	)
}