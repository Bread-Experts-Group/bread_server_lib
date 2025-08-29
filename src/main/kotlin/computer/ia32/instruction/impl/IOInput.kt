package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.imm8
import org.bread_experts_group.hex

class IOInput : InstructionCluster {
	class TwoOperand8Bit(
		opcode: UInt,
		val d: () -> String,
		val dc: Input2<UShort>
	) : Instruction(opcode, "in") {
		override fun operands(processor: IA32Processor): String = d()
		override fun handle(processor: IA32Processor) {
			val (dest, src) = dc()
			dest.second(processor.computer.getIODevice(src.first().toUInt()).read(processor.computer).toUShort())
		}
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		TwoOperand8Bit(
			0xE4u,
			{ "al, ${hex(processor.imm8())}" },
			{
				val imm8 = processor.imm8().toUShort()
				({ processor.a.tl.toUShort() } to { s: UShort -> processor.a.tl = s.toUByte() }) to
						({ imm8 } to {})
			}
		),
		TwoOperand8Bit(
			0xECu,
			{ "al, dx" },
			{
				({ processor.a.tl.toUShort() } to { s: UShort -> processor.a.tl = s.toUByte() }) to
						({ processor.d.tx } to {})
			}
		)
	)
}