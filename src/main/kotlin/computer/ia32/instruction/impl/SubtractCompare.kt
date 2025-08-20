package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.type.Instruction

class SubtractCompare : InstructionCluster {
	class TwoOperand8Bit(
		opcode: UInt,
		val d: () -> String,
		val dc: Input2<UByte>
	) : Instruction(opcode, "cmp") {
		override fun operands(processor: IA32Processor): String = d()
		override fun handle(processor: IA32Processor) {
			val (dest, src) = dc()
			setFlagsSFZFPF8(processor, subtractAndSetFlagsCFOF8(processor, dest.first(), src.first()))
		}
	}

	class TwoOperand(
		opcode: UInt,
		val d16: () -> String,
		val dc16: Input2<UShort>,
		val d32: () -> String,
		val dc32: Input2<UInt>
	) : Instruction(opcode, "cmp") {
		override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
			AddressingLength.R16 -> d16()
			AddressingLength.R32 -> d32()
			else -> throw UnsupportedOperationException()
		}

		override fun handle(processor: IA32Processor) {
			when (processor.operandSize) {
				AddressingLength.R16 -> {
					val (dest16, src16) = dc16()
					setFlagsSFZFPF16(processor, subtractAndSetFlagsCFOF16(processor, dest16.first(), src16.first()))
				}

				AddressingLength.R32 -> {
					val (dest32, src32) = dc32()
					setFlagsSFZFPF32(processor, subtractAndSetFlagsCFOF32(processor, dest32.first(), src32.first()))
				}

				else -> throw UnsupportedOperationException()
			}
		}
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		TwoOperand8Bit(
			0x38u,
			d8MR(processor), dc8MR(processor)
		),
		TwoOperand(
			0x39u,
			d16MR(processor), dc16MR(processor),
			d32MR(processor), dc32MR(processor)
		),
		TwoOperand8Bit(
			0x3Au,
			d8RM(processor), dc8RM(processor)
		),
		TwoOperand(
			0x3Bu,
			d16RM(processor), dc16RM(processor),
			d32RM(processor), dc32RM(processor)
		),
		TwoOperand8Bit(
			0x3Cu,
			d8ALImm(processor), dc8ALImm(processor)
		),
		TwoOperand(
			0x3Du,
			d16AXImm(processor), dc16AXImm(processor),
			d32EAXImm(processor), dc32EAXImm(processor)
		)
	)
}