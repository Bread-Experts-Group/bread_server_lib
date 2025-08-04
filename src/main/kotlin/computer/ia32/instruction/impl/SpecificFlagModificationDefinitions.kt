package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.assembler.Assembler
import org.bread_experts_group.computer.ia32.instruction.AssembledInstruction
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.register.FlagsRegister.FlagType
import java.io.OutputStream

class SpecificFlagModificationDefinitions : InstructionCluster {
	class SpecificFlagModification(
		opcode: UInt,
		n: Char,
		val flag: FlagType,
		val state: Boolean
	) : Instruction(opcode, "${if (state) "st" else "cl"}$n"), AssembledInstruction {
		override fun operands(processor: IA32Processor): String = ""
		override fun handle(processor: IA32Processor) {
			processor.flags.setFlag(this.flag, this.state)
		}

		override val arguments: Int = 0
		override fun acceptable(assembler: Assembler, from: ArrayDeque<String>): Boolean = true
		override fun produce(assembler: Assembler, into: OutputStream, from: ArrayDeque<String>) {
			into.write(this.opcode.toInt())
		}
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		SpecificFlagModification(0xF8u, 'c', FlagType.CARRY_FLAG, false),
		SpecificFlagModification(0xF9u, 'c', FlagType.CARRY_FLAG, true),
		SpecificFlagModification(0xFAu, 'i', FlagType.INTERRUPT_ENABLE_FLAG, false),
		SpecificFlagModification(0xFBu, 'i', FlagType.INTERRUPT_ENABLE_FLAG, true),
		SpecificFlagModification(0xFCu, 'd', FlagType.DIRECTION_FLAG, false),
		SpecificFlagModification(0xFDu, 'd', FlagType.DIRECTION_FLAG, true)
	)
}