package org.bread_experts_group.computer.ia32.instruction.impl.group.h80

import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.flag.ArithmeticAdditionFlagOperations
import org.bread_experts_group.computer.ia32.instruction.type.operand.Immediate8
import org.bread_experts_group.computer.ia32.instruction.type.operand.ModRM

object AddImmediateToModRM8 : Instruction(0u, "add"), ModRM, Immediate8, ArithmeticAdditionFlagOperations {
	override fun operands(processor: IA32Processor): String = processor.rmD(AddressingLength.R8).let {
		"${it.regMem}, ${hex(processor.imm8())}"
	}

	override fun handle(processor: IA32Processor) {
		val (memRM, _) = processor.rm(AddressingLength.R8)
		val result = this.setFlagsForOperationR(processor, memRM.getRMb(), processor.imm8())
		memRM.setRMb(result)
		this.setFlagsForResult(processor, result)
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}