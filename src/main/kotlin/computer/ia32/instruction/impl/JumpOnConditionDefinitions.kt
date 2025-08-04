package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.assembler.Assembler
import org.bread_experts_group.computer.ia32.instruction.AssembledInstruction
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.operand.Immediate16
import org.bread_experts_group.computer.ia32.instruction.type.operand.Immediate32
import org.bread_experts_group.computer.ia32.register.FlagsRegister.FlagType
import java.io.OutputStream

class JumpOnConditionDefinitions : InstructionCluster {
	class JumpOnConditionImmediateDisplacement(
		opcode: UInt,
		name: String,
		val condition: ((FlagType) -> Boolean) -> Boolean
	) : Instruction(opcode, "j$name"), Immediate16, Immediate32, AssembledInstruction {
		override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
			AddressingLength.R32 -> processor.rel32()
				.let { "${hex(it)} [${hex((processor.ip.tex.toInt() + it).toUInt())}]" }

			AddressingLength.R16 -> processor.rel16()
				.let { "${hex(it)} [${hex((processor.ip.tex.toInt() + it).toUInt())}]" }

			else -> throw UnsupportedOperationException()
		}

		override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
			AddressingLength.R32 -> {
				val rel32 = processor.rel32()
				if (this.condition(processor.flags::getFlag))
					processor.ip.tex = (processor.ip.tex.toInt() + rel32).toUInt()
				else {
				}
			}

			AddressingLength.R16 -> {
				val rel16 = processor.rel16()
				if (this.condition(processor.flags::getFlag))
					processor.ip.tex = (processor.ip.tex.toInt() + rel16).toUInt()
				else {
				}
			}

			else -> throw UnsupportedOperationException()
		}

		override val arguments: Int = 1
		override fun acceptable(assembler: Assembler, from: ArrayDeque<String>): Boolean {
			val jmpTo = assembler.readImmediate(from[0])
			return jmpTo != null
		}

		override fun produce(assembler: Assembler, into: OutputStream, from: ArrayDeque<String>) {
			if (opcode != 0xE9u) TODO(opcode.toString(16))
			val jmpTo = assembler.readImmediate(from.removeFirst())!!
			into.write(opcode.toInt())
			assembler.writeForMode(
				into,
				((jmpTo - assembler.position).toLong() - (1 + (assembler.mode.id / 8))).toULong()
			)
		}
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		JumpOnConditionImmediateDisplacement(0x0F80u, "o") { it(FlagType.OVERFLOW_FLAG) },
		JumpOnConditionImmediateDisplacement(0x0F81u, "no") { !it(FlagType.OVERFLOW_FLAG) },
		JumpOnConditionImmediateDisplacement(0x0F82u, "b") { it(FlagType.CARRY_FLAG) },
		JumpOnConditionImmediateDisplacement(0x0F83u, "nb") { !it(FlagType.CARRY_FLAG) },
		JumpOnConditionImmediateDisplacement(0x0F84u, "e") { it(FlagType.ZERO_FLAG) },
		JumpOnConditionImmediateDisplacement(0x0F85u, "ne") { !it(FlagType.ZERO_FLAG) },
		JumpOnConditionImmediateDisplacement(0x0F86u, "be") { it(FlagType.CARRY_FLAG) || it(FlagType.ZERO_FLAG) },
		JumpOnConditionImmediateDisplacement(0x0F87u, "nbe") { !it(FlagType.CARRY_FLAG) && !it(FlagType.ZERO_FLAG) },
		JumpOnConditionImmediateDisplacement(0x0F88u, "s") { it(FlagType.SIGN_FLAG) },
		JumpOnConditionImmediateDisplacement(0x0F89u, "ns") { !it(FlagType.SIGN_FLAG) },
		JumpOnConditionImmediateDisplacement(0x0F8Au, "p") { it(FlagType.PARITY_FLAG) },
		JumpOnConditionImmediateDisplacement(0x0F8Bu, "np") { !it(FlagType.PARITY_FLAG) },
		JumpOnConditionImmediateDisplacement(0x0F8Cu, "l") { it(FlagType.SIGN_FLAG) != it(FlagType.OVERFLOW_FLAG) },
		JumpOnConditionImmediateDisplacement(0x0F8Du, "nl") { it(FlagType.SIGN_FLAG) == it(FlagType.OVERFLOW_FLAG) },
		JumpOnConditionImmediateDisplacement(0x0F8Eu, "le") {
			it(FlagType.ZERO_FLAG) || (it(FlagType.SIGN_FLAG) != it(FlagType.OVERFLOW_FLAG))
		},
		JumpOnConditionImmediateDisplacement(0x0F8Fu, "nle") {
			!it(FlagType.ZERO_FLAG) && (it(FlagType.SIGN_FLAG) == it(FlagType.OVERFLOW_FLAG))
		},
		JumpOnConditionImmediateDisplacement(0xE9u, "mp") { true }
	)
}