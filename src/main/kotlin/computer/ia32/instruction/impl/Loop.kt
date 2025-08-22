package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.rel8
import org.bread_experts_group.computer.ia32.register.FlagsRegister.FlagType

class Loop : InstructionCluster {
	class ZeroOperand(
		opcode: UInt,
		s: String,
		val branch16: (IA32Processor) -> Boolean,
		val branch32: (IA32Processor) -> Boolean
	) : Instruction(opcode, "loop$s") {
		override fun operands(processor: IA32Processor): String {
			val rel8 = processor.rel8()
			return "${hex(rel8)} [${hex((processor.ip.tex.toInt() + rel8).toUInt())}]"
		}

		override fun handle(processor: IA32Processor) {
			val rel8 = processor.rel8()
			if (
				when (processor.operandSize) {
					AddressingLength.R32 -> branch32(processor)
					AddressingLength.R16 -> branch16(processor)
					else -> throw UnsupportedOperationException()
				}
			) processor.ip.tex = (processor.ip.tex.toInt() + rel8).toUInt()
		}
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		ZeroOperand(
			0xE0u, "nz",
			{ processor.c.x--; processor.c.x > 0uL && (!processor.flags.getFlag(FlagType.ZERO_FLAG)) },
			{ processor.c.ex--; processor.c.ex > 0uL && (!processor.flags.getFlag(FlagType.ZERO_FLAG)) }
		),
		ZeroOperand(
			0xE1u, "z",
			{ processor.c.x--; processor.c.x > 0uL && processor.flags.getFlag(FlagType.ZERO_FLAG) },
			{ processor.c.ex--; processor.c.ex > 0uL && processor.flags.getFlag(FlagType.ZERO_FLAG) }
		),
		ZeroOperand(
			0xE2u, "",
			{ processor.c.x--; processor.c.x > 0uL },
			{ processor.c.ex--; processor.c.ex > 0uL }
		)
	)
}