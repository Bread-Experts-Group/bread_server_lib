package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.assembler.Assembler
import org.bread_experts_group.computer.ia32.assembler.AssemblerMemRM.Companion.asmMemRM
import org.bread_experts_group.computer.ia32.assembler.AssemblerRegister.Companion.asmRegister
import org.bread_experts_group.computer.ia32.assembler.modRmByte
import org.bread_experts_group.computer.ia32.instruction.AssembledInstruction
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.RegisterType
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.operand.ModRM
import java.io.OutputStream

class MoveToRegisterDefinitions : InstructionCluster {
	class MoveModRMToRegister(
		opcode: UInt,
		type: RegisterType
	) : Instruction(opcode, "mov"), ModRM, AssembledInstruction {
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

		override val arguments: Int = 2
		override fun acceptable(assembler: Assembler, from: ArrayDeque<String>): Boolean {
			val register = from[0].asmRegister(assembler, assembler.mode, registerType)
			val memRM = from[1].asmMemRM(assembler, assembler.mode, RegisterType.GENERAL_PURPOSE)
			return register != null && memRM != null
		}

		override fun produce(assembler: Assembler, into: OutputStream, from: ArrayDeque<String>) {
			val register = from.removeFirst().asmRegister(assembler, assembler.mode, registerType)!!
			val memRM = from.removeFirst().asmMemRM(assembler, assembler.mode, RegisterType.GENERAL_PURPOSE)!!
			into.write(opcode.toInt())
			into.write(modRmByte(memRM, register))
		}
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