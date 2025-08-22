package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.type.Instruction

class AddWithCarry : InstructionCluster {
	class TwoOperand8Bit(
		opcode: UInt,
		val d: () -> String,
		val dc: Input2<UByte>
	) : Instruction(opcode, "adc") {
		override fun operands(processor: IA32Processor): String = d()
		override fun handle(processor: IA32Processor) {
			val (dest, src) = dc()
			val result = addAndSetFlagsAFCFOF8(processor, dest.first(), (src.first() + carry10b(processor)).toUByte())
			dest.second(result)
			setFlagsSFZFPF8(processor, result)
		}
	}

	class TwoOperand(
		opcode: UInt,
		val d16: () -> String,
		val dc16: Input2<UShort>,
		val d32: () -> String,
		val dc32: Input2<UInt>
	) : Instruction(opcode, "adc") {
		override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
			AddressingLength.R16 -> d16()
			AddressingLength.R32 -> d32()
			else -> throw UnsupportedOperationException()
		}

		override fun handle(processor: IA32Processor) {
			when (processor.operandSize) {
				AddressingLength.R16 -> {
					val (dest16, src16) = dc16()
					val result = addAndSetFlagsAFCFOF16(
						processor,
						dest16.first(), (src16.first() + carry10s(processor)).toUShort()
					)
					dest16.second(result)
					setFlagsSFZFPF16(processor, result)
				}

				AddressingLength.R32 -> {
					val (dest32, src32) = dc32()
					val result = addAndSetFlagsAFCFOF32(processor, dest32.first(), src32.first() + carry10i(processor))
					dest32.second(result)
					setFlagsSFZFPF32(processor, result)
				}

				else -> throw UnsupportedOperationException()
			}
		}
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		TwoOperand8Bit(
			0x10u,
			d8MR(processor), dc8MR(processor)
		),
		TwoOperand(
			0x11u,
			d16MR(processor), dc16MR(processor),
			d32MR(processor), dc32MR(processor)
		),
		TwoOperand8Bit(
			0x12u,
			d8RM(processor), dc8RM(processor)
		),
		TwoOperand(
			0x13u,
			d16RM(processor), dc16RM(processor),
			d32RM(processor), dc32RM(processor)
		),
		TwoOperand8Bit(
			0x14u,
			d8ALImm(processor), dc8ALImm(processor)
		),
		TwoOperand(
			0x15u,
			d16AXImm(processor), dc16AXImm(processor),
			d32EAXImm(processor), dc32EAXImm(processor)
		)
	)
}