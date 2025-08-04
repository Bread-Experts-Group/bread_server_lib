package org.bread_experts_group.computer.ia32.instruction.impl.h0F

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.RegisterType
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.operand.ModRM
import org.bread_experts_group.computer.ia32.register.FlagsRegister.FlagType

class SetByteToFlagDefinitions : InstructionCluster {
	class SetByteToFlag(
		opcode: UInt,
		val condition: ((FlagType) -> Boolean) -> Boolean,
		n: String
	) : Instruction(opcode, "set$n"), ModRM {
		override fun operands(processor: IA32Processor): String = processor.rmD(AddressingLength.R8).regMem
		override fun handle(processor: IA32Processor) {
			val (memRm, _) = processor.rm(AddressingLength.R8)
			memRm.setRMb(if (this.condition(processor.flags::getFlag)) 1u else 0u)
		}

		override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		SetByteToFlag(0x0F90u, { it(FlagType.OVERFLOW_FLAG) }, "o"),
		SetByteToFlag(0x0F91u, { !it(FlagType.OVERFLOW_FLAG) }, "no"),
		SetByteToFlag(0x0F92u, { it(FlagType.CARRY_FLAG) }, "c"),
		SetByteToFlag(0x0F93u, { !it(FlagType.CARRY_FLAG) }, "nc"),
		SetByteToFlag(0x0F94u, { it(FlagType.ZERO_FLAG) }, "z"),
		SetByteToFlag(0x0F95u, { !it(FlagType.ZERO_FLAG) }, "nz"),
		SetByteToFlag(0x0F96u, { it(FlagType.CARRY_FLAG) || it(FlagType.ZERO_FLAG) }, "be"),
		SetByteToFlag(0x0F97u, { !it(FlagType.CARRY_FLAG) && !it(FlagType.ZERO_FLAG) }, "nbe"),
		SetByteToFlag(0x0F98u, { it(FlagType.SIGN_FLAG) }, "s"),
		SetByteToFlag(0x0F99u, { !it(FlagType.SIGN_FLAG) }, "ns"),
		SetByteToFlag(0x0F9Au, { it(FlagType.PARITY_FLAG) }, "p"),
		SetByteToFlag(0x0F9Bu, { !it(FlagType.PARITY_FLAG) }, "np"),
		SetByteToFlag(0x0F9Cu, { it(FlagType.SIGN_FLAG) != it(FlagType.OVERFLOW_FLAG) }, "l"),
		SetByteToFlag(0x0F9Du, { it(FlagType.SIGN_FLAG) == it(FlagType.OVERFLOW_FLAG) }, "nl"),
		SetByteToFlag(
			0x0F9Eu, { it(FlagType.ZERO_FLAG) || (it(FlagType.SIGN_FLAG) != it(FlagType.OVERFLOW_FLAG)) },
			"le"
		),
		SetByteToFlag(
			0x0F9Fu, { !it(FlagType.ZERO_FLAG) && (it(FlagType.SIGN_FLAG) == it(FlagType.OVERFLOW_FLAG)) },
			"nle"
		)
	)
}