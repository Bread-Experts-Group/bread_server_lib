package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.operand.ModRM

class MoveToRegisterDefinitions : InstructionCluster {
	class MoveModRMToRegister(opcode: UInt, type: RegisterType) : Instruction(opcode, "mov"), ModRM {
		override fun operands(processor: IA32Processor): String = processor.rmD().let {
			"${it.register}, ${it.regMem}"
		}

		override fun handle(processor: IA32Processor) {
			val (memRM, register) = processor.rm()
			when (processor.operandSize) {
				AddressingLength.R32 -> register.set(memRM.getRMi().toULong())
				AddressingLength.R16 -> register.set(memRM.getRMs().toULong())
				else -> throw UnsupportedOperationException()
			}
		}

		override val registerType: RegisterType = type
	}

	class MoveModRMToRegister8(opcode: UInt, type: RegisterType) : Instruction(opcode, "mov"), ModRM {
		override fun operands(processor: IA32Processor): String = processor.rmD(AddressingLength.R8).let {
			"${it.register}, ${it.regMem}"
		}

		override fun handle(processor: IA32Processor) {
			val (memRM, register) = processor.rm(AddressingLength.R8)
			register.set(memRM.getRMb().toULong())
		}

		override val registerType: RegisterType = type
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		MoveModRMToRegister(0x8Bu, RegisterType.GENERAL_PURPOSE),
		MoveModRMToRegister(0x8Eu, RegisterType.SEGMENT),
		MoveModRMToRegister8(0x8Au, RegisterType.GENERAL_PURPOSE)
	)
}