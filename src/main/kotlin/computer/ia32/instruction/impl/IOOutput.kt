package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.BinaryUtil.shr
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.imm8
import org.bread_experts_group.hex

class IOOutput : InstructionCluster {
	class TwoOperand8Bit(
		opcode: UInt,
		val d: () -> String,
		val dc: Input2<UShort>
	) : Instruction(opcode, "out") {
		override fun operands(processor: IA32Processor): String = d()
		override fun handle(processor: IA32Processor) {
			val (dest, src) = dc()
			processor.computer.getIODevice(dest.first().toUInt()).write(processor.computer, src.first().toUByte())
		}
	}

	class TwoOperand(
		opcode: UInt,
		val d16: () -> String,
		val dc16: Input2<UShort>,
		val d32: () -> String,
		val dc32: Input2<UInt>
	) : Instruction(opcode, "out") {
		override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
			AddressingLength.R32 -> d32()
			AddressingLength.R16 -> d16()
			else -> throw UnsupportedOperationException()
		}

		override fun handle(processor: IA32Processor) {
			when (processor.operandSize) {
				AddressingLength.R32 -> {
					val (dest32, src32) = dc32()
					val io = processor.computer.getIODevice(dest32.first())
					val write = src32.first()
					io.write(processor.computer, (write shr 24).toUByte())
					io.write(processor.computer, (write shr 16).toUByte())
					io.write(processor.computer, (write shr 8).toUByte())
					io.write(processor.computer, (write).toUByte())
				}

				AddressingLength.R16 -> {
					val (dest16, src16) = dc16()
					val io = processor.computer.getIODevice(dest16.first().toUInt())
					val write = src16.first()
					io.write(processor.computer, (write shr 8).toUByte())
					io.write(processor.computer, (write).toUByte())
				}

				else -> throw UnsupportedOperationException()
			}
		}
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		TwoOperand8Bit(
			0xE6u,
			{ "${hex(processor.imm8())}, al" },
			{
				val port = processor.imm8().toUShort()
				({ port } to { _: UShort -> }) to ({ processor.a.tl.toUShort() } to {})
			}
		),
		TwoOperand(
			0xE7u,
			{ "${hex(processor.imm8())}, ax" },
			{
				val port = processor.imm8().toUShort()
				({ port } to { _: UShort -> }) to ({ processor.a.tx } to {})
			},
			{ "${hex(processor.imm8())}, eax" },
			{
				val port = processor.imm8().toUInt()
				({ port } to { _: UInt -> }) to ({ processor.a.tex } to {})
			},
		),
		TwoOperand8Bit(
			0xEEu,
			{ "dx, al" },
			{
				({ processor.d.tx } to { _: UShort -> }) to ({ processor.a.tl.toUShort() } to {})
			}
		),
		TwoOperand(
			0xEFu,
			{ "dx, ax" },
			{
				({ processor.d.tx } to { _: UShort -> }) to ({ processor.a.tx } to {})
			},
			{ "dx, eax" },
			{
				({ processor.d.tex } to { _: UInt -> }) to ({ processor.a.tex } to {})
			},
		)
	)
}