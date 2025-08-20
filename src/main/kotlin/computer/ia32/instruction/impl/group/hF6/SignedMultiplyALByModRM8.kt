package org.bread_experts_group.computer.ia32.instruction.impl.group.hF6

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.RegisterType
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.operand.ModRM
import org.bread_experts_group.computer.ia32.register.FlagsRegister.FlagType

object SignedMultiplyALByModRM8 : Instruction(0u, "imul"), ModRM {
	override fun operands(processor: IA32Processor): String = processor.rmD(AddressingLength.R8).regMem

	override fun handle(processor: IA32Processor) {
		val (memRM, _) = processor.rm(AddressingLength.R8)
		processor.a.x = (processor.a.l.toByte().toShort() * memRM.getRMb().toByte().toShort()).toULong()
		processor.flags.setFlag(FlagType.OVERFLOW_FLAG, processor.a.h > 0u)
		processor.flags.setFlag(FlagType.CARRY_FLAG, processor.a.h > 0u)
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}