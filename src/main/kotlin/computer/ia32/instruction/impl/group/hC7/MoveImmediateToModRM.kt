package org.bread_experts_group.computer.ia32.instruction.impl.group.hC7

import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.assembler.Assembler
import org.bread_experts_group.computer.ia32.assembler.AssemblerMemRM.Companion.asmMemRM
import org.bread_experts_group.computer.ia32.assembler.AssemblerRegister
import org.bread_experts_group.computer.ia32.assembler.modRmByte
import org.bread_experts_group.computer.ia32.instruction.AssembledInstruction
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.RegisterType
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.operand.Immediate16
import org.bread_experts_group.computer.ia32.instruction.type.operand.Immediate32
import org.bread_experts_group.computer.ia32.instruction.type.operand.ModRM
import java.io.OutputStream

class MoveImmediateToModRM : Instruction(0u, "mov"), ModRM, Immediate32, Immediate16, AssembledInstruction {
	override fun operands(processor: IA32Processor): String = processor.rmD().let {
		when (processor.operandSize) {
			AddressingLength.R32 -> "${it.regMem}, ${hex(processor.imm32())}"
			AddressingLength.R16 -> "${it.regMem}, ${hex(processor.imm16())}"
			else -> throw UnsupportedOperationException()
		}
	}

	override fun handle(processor: IA32Processor) {
		val (memRM, _) = processor.rm()
		when (processor.operandSize) {
			AddressingLength.R32 -> memRM.setRMi(processor.imm32())
			AddressingLength.R16 -> memRM.setRMs(processor.imm16())
			else -> throw UnsupportedOperationException()
		}
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE

	override val arguments: Int = 2
	override fun acceptable(assembler: Assembler, from: ArrayDeque<String>): Boolean {
		val modRm = from[0].asmMemRM(assembler, assembler.mode, RegisterType.GENERAL_PURPOSE)
		val immediate = assembler.readImmediate(from[1])
		return modRm != null && immediate != null
	}

	override fun produce(assembler: Assembler, into: OutputStream, from: ArrayDeque<String>) {
		val modRm = from.removeFirst().asmMemRM(assembler, assembler.mode, RegisterType.GENERAL_PURPOSE)!!
		val immediate = assembler.readImmediate(from.removeFirst())!!
		into.write(0xC7)
		into.write(this.opcode.toInt())
		into.write(modRmByte(modRm, AssemblerRegister.CR0))
		assembler.writeForMode(into, immediate)
	}
}