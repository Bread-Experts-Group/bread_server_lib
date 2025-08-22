package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.rel8
import org.bread_experts_group.computer.ia32.register.FlagsRegister.FlagType

class JumpOnConditionDefinitions8 : InstructionCluster {
	class JumpOnConditionImmediate8Displacement(
		opcode: UInt,
		name: String,
		val condition: (IA32Processor, (FlagType) -> Boolean) -> Boolean
	) : Instruction(opcode, "j$name") {
		override fun operands(processor: IA32Processor): String = processor.rel8().let {
			"${hex(it)} [${hex((processor.ip.tex.toInt() + it).toUInt())}]"
		}

		override fun handle(processor: IA32Processor) {
			val rel8 = processor.rel8()
			if (this.condition(processor, processor.flags::getFlag))
				processor.ip.tex = (processor.ip.tex.toInt() + rel8).toUInt()
		}
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		JumpOnConditionImmediate8Displacement(0x70u, "o") { _, f -> f(FlagType.OVERFLOW_FLAG) },
		JumpOnConditionImmediate8Displacement(0x71u, "no") { _, f -> !f(FlagType.OVERFLOW_FLAG) },
		JumpOnConditionImmediate8Displacement(0x72u, "c") { _, f -> f(FlagType.CARRY_FLAG) },
		JumpOnConditionImmediate8Displacement(0x73u, "nc") { _, f -> !f(FlagType.CARRY_FLAG) },
		JumpOnConditionImmediate8Displacement(0x74u, "e") { _, f -> f(FlagType.ZERO_FLAG) },
		JumpOnConditionImmediate8Displacement(0x75u, "ne") { _, f -> !f(FlagType.ZERO_FLAG) },
		JumpOnConditionImmediate8Displacement(0x76u, "be") { _, f -> f(FlagType.CARRY_FLAG) || f(FlagType.ZERO_FLAG) },
		JumpOnConditionImmediate8Displacement(0x77u, "a") { _, f -> !f(FlagType.CARRY_FLAG) && !f(FlagType.ZERO_FLAG) },
		JumpOnConditionImmediate8Displacement(0x78u, "s") { _, f -> f(FlagType.SIGN_FLAG) },
		JumpOnConditionImmediate8Displacement(0x79u, "ns") { _, f -> !f(FlagType.SIGN_FLAG) },
		JumpOnConditionImmediate8Displacement(0x7Au, "p") { _, f -> f(FlagType.PARITY_FLAG) },
		JumpOnConditionImmediate8Displacement(0x7Bu, "np") { _, f -> !f(FlagType.PARITY_FLAG) },
		JumpOnConditionImmediate8Displacement(0x7Cu, "l") { _, f ->
			f(FlagType.SIGN_FLAG) != f(FlagType.OVERFLOW_FLAG)
		},
		JumpOnConditionImmediate8Displacement(0x7Du, "ge") { _, f ->
			f(FlagType.SIGN_FLAG) == f(FlagType.OVERFLOW_FLAG)
		},
		JumpOnConditionImmediate8Displacement(0x7Eu, "le") { _, f ->
			f(FlagType.ZERO_FLAG) || (f(FlagType.SIGN_FLAG) != f(FlagType.OVERFLOW_FLAG))
		},
		JumpOnConditionImmediate8Displacement(0x7Fu, "g") { _, f ->
			!f(FlagType.ZERO_FLAG) && (f(FlagType.SIGN_FLAG) == f(FlagType.OVERFLOW_FLAG))
		},
		JumpOnConditionImmediate8Displacement(0xEBu, "mp") { _, f -> true },
		JumpOnConditionImmediate8Displacement(0xE3u, "cxz") { p, _ ->
			when (p.operandSize) {
				AddressingLength.R32 -> p.c.ex == 0uL
				AddressingLength.R16 -> p.c.x == 0uL
				else -> throw UnsupportedOperationException()
			}
		}
	)
}