package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.operand.Immediate8
import org.bread_experts_group.computer.ia32.register.FlagsRegister.FlagType

class JumpOnConditionDefinitions8 : InstructionCluster {
	class JumpOnConditionImmediate8Displacement(
		opcode: UInt,
		name: String,
		val condition: ((FlagType) -> Boolean) -> Boolean
	) : Instruction(opcode, "j$name"), Immediate8 {
		override fun operands(processor: IA32Processor): String = processor.rel8().let {
			"${hex(it)} [${hex((processor.ip.tex.toInt() + it).toUInt())}]"
		}

		override fun handle(processor: IA32Processor) {
			val rel8 = processor.rel8()
			if (this.condition(processor.flags::getFlag))
				processor.ip.tex = (processor.ip.tex.toInt() + rel8).toUInt()
		}
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		JumpOnConditionImmediate8Displacement(0x70u, "o") { it(FlagType.OVERFLOW_FLAG) },
		JumpOnConditionImmediate8Displacement(0x71u, "no") { !it(FlagType.OVERFLOW_FLAG) },
		JumpOnConditionImmediate8Displacement(0x72u, "c") { it(FlagType.CARRY_FLAG) },
		JumpOnConditionImmediate8Displacement(0x73u, "nc") { !it(FlagType.CARRY_FLAG) },
		JumpOnConditionImmediate8Displacement(0x74u, "e") { it(FlagType.ZERO_FLAG) },
		JumpOnConditionImmediate8Displacement(0x75u, "ne") { !it(FlagType.ZERO_FLAG) },
		JumpOnConditionImmediate8Displacement(0x76u, "be") { it(FlagType.CARRY_FLAG) || it(FlagType.ZERO_FLAG) },
		JumpOnConditionImmediate8Displacement(0x77u, "a") { !it(FlagType.CARRY_FLAG) && !it(FlagType.ZERO_FLAG) },
		JumpOnConditionImmediate8Displacement(0x78u, "s") { it(FlagType.SIGN_FLAG) },
		JumpOnConditionImmediate8Displacement(0x79u, "s") { !it(FlagType.SIGN_FLAG) },
		JumpOnConditionImmediate8Displacement(0x7Au, "p") { it(FlagType.PARITY_FLAG) },
		JumpOnConditionImmediate8Displacement(0x7Bu, "np") { !it(FlagType.PARITY_FLAG) },
		JumpOnConditionImmediate8Displacement(0x7Cu, "l") { it(FlagType.SIGN_FLAG) != it(FlagType.OVERFLOW_FLAG) },
		JumpOnConditionImmediate8Displacement(0x7Du, "ge") { it(FlagType.SIGN_FLAG) == it(FlagType.OVERFLOW_FLAG) },
		JumpOnConditionImmediate8Displacement(0x7Eu, "le") {
			it(FlagType.ZERO_FLAG) || (it(FlagType.SIGN_FLAG) != it(FlagType.OVERFLOW_FLAG))
		},
		JumpOnConditionImmediate8Displacement(0x7Fu, "g") {
			!it(FlagType.ZERO_FLAG) && (it(FlagType.SIGN_FLAG) == it(FlagType.OVERFLOW_FLAG))
		},
		JumpOnConditionImmediate8Displacement(0xEBu, "mp") { true }
	)
}