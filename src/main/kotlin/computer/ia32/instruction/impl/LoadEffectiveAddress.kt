package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.RegisterType
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.operand.ModRM

class LoadEffectiveAddress : Instruction(0x8Du, "lea"), ModRM {
	override fun operands(processor: IA32Processor): String = processor.rmD().let { "${it.register}, ${it.regMem}" }
	override fun handle(processor: IA32Processor) {
		val (memRM, register) = processor.rm()
		register.set(memRM.memory!!)
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}