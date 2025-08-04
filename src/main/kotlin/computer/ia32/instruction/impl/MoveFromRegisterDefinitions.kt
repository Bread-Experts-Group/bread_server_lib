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

class MoveFromRegisterDefinitions : InstructionCluster {
	class MoveRegisterToModRM(
		opcode: UInt,
		type: RegisterType
	) : Instruction(opcode, "mov"), ModRM, AssembledInstruction {
		override fun operands(processor: IA32Processor): String = processor.rmD().let { "${it.regMem}, ${it.register}" }
		override fun handle(processor: IA32Processor) {
			val (memRM, register) = processor.rm()
			when (processor.operandSize) {
				AddressingLength.R32 -> memRM.setRMi(register.get().toUInt())
				AddressingLength.R16 -> memRM.setRMs(register.get().toUShort())
				else -> throw UnsupportedOperationException()
			}
		}

		override val registerType: RegisterType = type

		override val arguments: Int = 2
		override fun acceptable(assembler: Assembler, from: ArrayDeque<String>): Boolean {
			val memRM = from[0].asmMemRM(assembler, assembler.mode, RegisterType.GENERAL_PURPOSE)
			val register = from[1].asmRegister(assembler, assembler.mode, registerType)
			return register != null && memRM != null
		}

		override fun produce(assembler: Assembler, into: OutputStream, from: ArrayDeque<String>) {
			val memRM = from.removeFirst().asmMemRM(assembler, assembler.mode, RegisterType.GENERAL_PURPOSE)!!
			val register = from.removeFirst().asmRegister(assembler, assembler.mode, registerType)!!
			into.write(opcode.toInt())
			into.write(modRmByte(memRM, register))
		}
	}

	class MoveRegister8ToModRM(opcode: UInt, type: RegisterType) : Instruction(opcode, "mov"), ModRM {
		override fun operands(processor: IA32Processor): String = processor.rmD(AddressingLength.R8).let {
			"${it.regMem}, ${it.register}"
		}

		override fun handle(processor: IA32Processor) {
			val (memRM, register) = processor.rm(AddressingLength.R8)
			memRM.setRMb(register.get().toUByte())
		}

		override val registerType: RegisterType = type
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		MoveRegisterToModRM(0x89u, RegisterType.GENERAL_PURPOSE),
		MoveRegisterToModRM(0x8Cu, RegisterType.SEGMENT),
		MoveRegister8ToModRM(0x88u, RegisterType.GENERAL_PURPOSE)
	)
}