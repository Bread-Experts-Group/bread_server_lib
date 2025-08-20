package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.RegisterType
import org.bread_experts_group.computer.ia32.instruction.type.Instruction

class Move : InstructionCluster {
	class TwoOperand8Bit(
		opcode: UInt,
		val d: () -> String,
		val dc: Input2<UByte>
	) : Instruction(opcode, "mov") {
		override fun operands(processor: IA32Processor): String = d()
		override fun handle(processor: IA32Processor) {
			val (dest, src) = dc()
			dest.second(src.first())
		}
	}

	class TwoOperand(
		opcode: UInt,
		val d16: () -> String,
		val dc16: Input2<UShort>,
		val d32: () -> String,
		val dc32: Input2<UInt>
	) : Instruction(opcode, "mov") {
		override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
			AddressingLength.R16 -> d16()
			AddressingLength.R32 -> d32()
			else -> throw UnsupportedOperationException()
		}

		override fun handle(processor: IA32Processor) {
			when (processor.operandSize) {
				AddressingLength.R16 -> {
					val (dest16, src16) = dc16()
					dest16.second(src16.first())
				}

				AddressingLength.R32 -> {
					val (dest32, src32) = dc32()
					dest32.second(src32.first())
				}

				else -> throw UnsupportedOperationException()
			}
		}
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		TwoOperand8Bit(0x88u, d8MR(processor), dc8MR(processor)),
		TwoOperand(
			0x89u,
			d16MR(processor), dc16MR(processor),
			d32MR(processor), dc32MR(processor)
		),
		TwoOperand8Bit(0x8Au, d8RM(processor), dc8RM(processor)),
		TwoOperand(
			0x8Bu,
			d16RM(processor), dc16RM(processor),
			d32RM(processor), dc32RM(processor)
		),
		TwoOperand(
			0x8Cu,
			d16MR(processor, RegisterType.SEGMENT), dc16MR(processor, RegisterType.SEGMENT),
			d32MR(processor, RegisterType.SEGMENT), dc32MR(processor, RegisterType.SEGMENT)
		),
		TwoOperand(
			0x8Eu,
			d16RM(processor, RegisterType.SEGMENT), dc16RM(processor, RegisterType.SEGMENT),
			d16RM(processor, RegisterType.SEGMENT), dc32RM(processor, RegisterType.SEGMENT)
		),
		TwoOperand8Bit(0xA0u, d8FD(processor), dc8FD(processor)),
		TwoOperand(
			0xA1u,
			d16FD(processor), dc16FD(processor),
			d32FD(processor), dc32FD(processor)
		),
		TwoOperand8Bit(0xA2u, d8TD(processor), dc8TD(processor)),
		TwoOperand(
			0xA3u,
			d16TD(processor), dc16TD(processor),
			d32TD(processor), dc32TD(processor)
		),
		TwoOperand8Bit(0xB0u, d8OI(processor, "al"), dc8OI(processor, processor.a::tl)),
		TwoOperand8Bit(0xB1u, d8OI(processor, "cl"), dc8OI(processor, processor.c::tl)),
		TwoOperand8Bit(0xB2u, d8OI(processor, "dl"), dc8OI(processor, processor.d::tl)),
		TwoOperand8Bit(0xB3u, d8OI(processor, "bl"), dc8OI(processor, processor.b::tl)),
		TwoOperand8Bit(0xB4u, d8OI(processor, "ah"), dc8OI(processor, processor.a::th)),
		TwoOperand8Bit(0xB5u, d8OI(processor, "ch"), dc8OI(processor, processor.c::th)),
		TwoOperand8Bit(0xB6u, d8OI(processor, "dh"), dc8OI(processor, processor.d::th)),
		TwoOperand8Bit(0xB7u, d8OI(processor, "bh"), dc8OI(processor, processor.b::th)),
		TwoOperand(
			0xB8u,
			d16OI(processor, "ax"), dc16OI(processor, processor.a::tx),
			d32OI(processor, "eax"), dc32OI(processor, processor.a::tex),
		),
		TwoOperand(
			0xB9u,
			d16OI(processor, "cx"), dc16OI(processor, processor.c::tx),
			d32OI(processor, "ecx"), dc32OI(processor, processor.c::tex),
		),
		TwoOperand(
			0xBAu,
			d16OI(processor, "dx"), dc16OI(processor, processor.d::tx),
			d32OI(processor, "edx"), dc32OI(processor, processor.d::tex),
		),
		TwoOperand(
			0xBBu,
			d16OI(processor, "bx"), dc16OI(processor, processor.b::tx),
			d32OI(processor, "ebx"), dc32OI(processor, processor.b::tex),
		),
		TwoOperand(
			0xBCu,
			d16OI(processor, "sp"), dc16OI(processor, processor.sp::tx),
			d32OI(processor, "esp"), dc32OI(processor, processor.sp::tex),
		),
		TwoOperand(
			0xBDu,
			d16OI(processor, "bp"), dc16OI(processor, processor.bp::tx),
			d32OI(processor, "ebp"), dc32OI(processor, processor.bp::tex),
		),
		TwoOperand(
			0xBEu,
			d16OI(processor, "si"), dc16OI(processor, processor.si::tx),
			d32OI(processor, "esi"), dc32OI(processor, processor.si::tex),
		),
		TwoOperand(
			0xBFu,
			d16OI(processor, "di"), dc16OI(processor, processor.di::tx),
			d32OI(processor, "edi"), dc32OI(processor, processor.di::tex),
		),
	)
}