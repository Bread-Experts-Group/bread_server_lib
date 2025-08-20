package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.type.Instruction

class Exchange : InstructionCluster {
	class TwoOperand8Bit(
		opcode: UInt,
		val d: () -> String,
		val dc: Input2<UByte>
	) : Instruction(opcode, "xchg") {
		override fun operands(processor: IA32Processor): String = d()
		override fun handle(processor: IA32Processor) {
			val (dest, src) = dc()
			val temp = dest.first()
			dest.second(src.first())
			src.second(temp)
		}
	}

	class TwoOperand(
		opcode: UInt,
		val d16: () -> String,
		val dc16: Input2<UShort>,
		val d32: () -> String,
		val dc32: Input2<UInt>
	) : Instruction(opcode, "xchg") {
		override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
			AddressingLength.R32 -> d32()
			AddressingLength.R16 -> d16()
			else -> throw UnsupportedOperationException()
		}

		override fun handle(processor: IA32Processor) {
			when (processor.operandSize) {
				AddressingLength.R32 -> {
					val (dest, src) = dc32()
					val temp = dest.first()
					dest.second(src.first())
					src.second(temp)
				}

				AddressingLength.R16 -> {
					val (dest, src) = dc16()
					val temp = dest.first()
					dest.second(src.first())
					src.second(temp)
				}

				else -> throw UnsupportedOperationException()
			}
		}
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		TwoOperand8Bit(0x86u, d8RM(processor), dc8RM(processor)),
		TwoOperand(
			0x87u,
			d16RM(processor), dc16RM(processor),
			d32RM(processor), dc32RM(processor),
		),
		TwoOperand(
			0x90u,
			d16Oax(processor, "ax"), dc16Oax(processor, processor.a::tx),
			d32Oeax(processor, "eax"), dc32Oeax(processor, processor.a::tex),
		),
		TwoOperand(
			0x91u,
			d16Oax(processor, "cx"), dc16Oax(processor, processor.c::tx),
			d32Oeax(processor, "ecx"), dc32Oeax(processor, processor.c::tex),
		),
		TwoOperand(
			0x92u,
			d16Oax(processor, "dx"), dc16Oax(processor, processor.d::tx),
			d32Oeax(processor, "edx"), dc32Oeax(processor, processor.d::tex),
		),
		TwoOperand(
			0x93u,
			d16Oax(processor, "bx"), dc16Oax(processor, processor.b::tx),
			d32Oeax(processor, "ebx"), dc32Oeax(processor, processor.b::tex),
		),
		TwoOperand(
			0x94u,
			d16Oax(processor, "sp"), dc16Oax(processor, processor.sp::tx),
			d32Oeax(processor, "esp"), dc32Oeax(processor, processor.sp::tex),
		),
		TwoOperand(
			0x95u,
			d16Oax(processor, "bp"), dc16Oax(processor, processor.bp::tx),
			d32Oeax(processor, "ebp"), dc32Oeax(processor, processor.bp::tex),
		),
		TwoOperand(
			0x96u,
			d16Oax(processor, "si"), dc16Oax(processor, processor.si::tx),
			d32Oeax(processor, "esi"), dc32Oeax(processor, processor.si::tex),
		),
		TwoOperand(
			0x97u,
			d16Oax(processor, "di"), dc16Oax(processor, processor.di::tx),
			d32Oeax(processor, "edi"), dc32Oeax(processor, processor.di::tex),
		),
	)
}