package org.bread_experts_group.computer.ia32.instruction.impl.h0F

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.RegisterType
import org.bread_experts_group.computer.ia32.instruction.type.Instruction

class MoveWithZeroExtension8 : Instruction(0x0FB6u, "movzx") {
	override fun operands(processor: IA32Processor): String {
		TODO("AMIS")
		val rmByte = processor.decoding.readFetch()
		val rm8D = processor.decoding.getModRMDisassembler(rmByte, RegisterType.GENERAL_PURPOSE, AddressingLength.R8)
		val rmD = processor.decoding.getModRMDisassembler(rmByte, RegisterType.GENERAL_PURPOSE)
		return "${rmD.register}, ${rm8D.regMem}"
	}

	override fun handle(processor: IA32Processor) {
		TODO("AMIS")
		val rmByte = processor.decoding.readFetch()
		val (regMem8) = processor.decoding.getModRM(rmByte, RegisterType.GENERAL_PURPOSE, AddressingLength.R8)
		val (_, reg) = processor.decoding.getModRM(rmByte, RegisterType.GENERAL_PURPOSE)
		reg.set(regMem8.getRMb().toULong())
	}
}