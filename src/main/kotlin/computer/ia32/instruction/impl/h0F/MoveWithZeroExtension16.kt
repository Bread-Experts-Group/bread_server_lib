package org.bread_experts_group.computer.ia32.instruction.impl.h0F

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.RegisterType
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.operand.ModRM

class MoveWithZeroExtension16 : Instruction(0x0FB7u, "movzx"), ModRM {
	override fun operands(processor: IA32Processor): String {
		val saved = processor.ip.rx
		val rm16D = processor.rmD(AddressingLength.R16)
		processor.ip.rx = saved
		val rmD = processor.rmD()
		return "${rmD.register}, ${rm16D.regMem}"
	}

	override fun handle(processor: IA32Processor) {
		val saved = processor.ip.rx
		val (regMem16) = processor.rm(AddressingLength.R16)
		processor.ip.rx = saved
		val (_, reg) = processor.rm()
		reg.set(regMem16.getRMs().toULong())
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}