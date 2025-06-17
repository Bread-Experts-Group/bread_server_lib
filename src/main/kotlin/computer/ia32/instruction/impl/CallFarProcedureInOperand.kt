package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.operand.Immediate16
import org.bread_experts_group.computer.ia32.instruction.type.operand.Immediate32

class CallFarProcedureInOperand : Instruction(0x9Au, "callf"), Immediate32, Immediate16 {
	override fun operands(processor: IA32Processor): String {
		TODO("Not yet implemented")
	}

	override fun handle(processor: IA32Processor) {
		TODO("Not yet implemented")
	}
}