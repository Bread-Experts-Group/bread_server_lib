package org.bread_experts_group.computer.ia32.instruction.impl.group.hC7

import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.operand.Immediate16
import org.bread_experts_group.computer.ia32.instruction.type.operand.Immediate32
import org.bread_experts_group.computer.ia32.instruction.type.operand.ModRM

object MoveImmediateToModRM : Instruction(0u, "mov"), ModRM, Immediate32, Immediate16 {
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
}