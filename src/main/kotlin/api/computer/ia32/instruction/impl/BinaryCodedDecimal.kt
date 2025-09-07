package org.bread_experts_group.api.computer.ia32.instruction.impl

import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.api.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.api.computer.ia32.instruction.type.imm8
import org.bread_experts_group.api.computer.ia32.register.FlagsRegister.FlagType
import org.bread_experts_group.hex

class BinaryCodedDecimal : InstructionCluster {
	object DecimalAdjustALAfterAddition : Instruction(0x27u, "daa") {
		override fun operands(processor: IA32Processor): String = ""
		override fun handle(processor: IA32Processor) {
			val sAL = processor.a.l
			val sCF = processor.flags.getFlag(FlagType.CARRY_FLAG)
			processor.flags.setFlag(FlagType.CARRY_FLAG, false)
			if (((sAL and 0x0Fu) > 9u) || processor.flags.getFlag(FlagType.AUXILIARY_CARRY_FLAG)) {
				val result = addAndSetFlagsAFCFOF8(processor, processor.a.tl, 6u)
				processor.a.tl = result
				if (!processor.flags.getFlag(FlagType.CARRY_FLAG))
					processor.flags.setFlag(FlagType.CARRY_FLAG, sCF)
				processor.flags.setFlag(FlagType.AUXILIARY_CARRY_FLAG, true)
			} else processor.flags.setFlag(FlagType.AUXILIARY_CARRY_FLAG, false)
			if ((sAL > 0x99u) || sCF) {
				processor.a.l += 0x60u
				processor.flags.setFlag(FlagType.CARRY_FLAG, true)
			} else processor.flags.setFlag(FlagType.CARRY_FLAG, false)
			setFlagsSFZFPF8(processor, processor.a.tl)
		}
	}

	object ASCIIAdjustAXAfterMultiply : Instruction(0xD4u, "aam") {
		override fun operands(processor: IA32Processor): String = hex(processor.imm8())
		override fun handle(processor: IA32Processor) {
			val sAL = processor.a.l
			val imm8 = processor.imm8()
			processor.a.h = sAL / imm8
			processor.a.l = sAL % imm8
			setFlagsSFZFPF8(processor, processor.a.tl)
		}
	}

	object ASCIIAdjustAXBeforeDivision : Instruction(0xD5u, "aad") {
		override fun operands(processor: IA32Processor): String = hex(processor.imm8())
		override fun handle(processor: IA32Processor) {
			processor.a.tl = (processor.a.tl + (processor.a.th * processor.imm8())).toUByte()
			processor.a.th = 0u
			setFlagsSFZFPF8(processor, processor.a.tl)
		}
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		DecimalAdjustALAfterAddition,
		ASCIIAdjustAXAfterMultiply,
		ASCIIAdjustAXBeforeDivision
	)
}