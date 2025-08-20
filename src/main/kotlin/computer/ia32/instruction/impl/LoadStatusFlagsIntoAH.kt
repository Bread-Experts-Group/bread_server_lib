package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.type.Instruction

class LoadStatusFlagsIntoAH : Instruction(0x9Fu, "lahf") {
	override fun operands(processor: IA32Processor): String = ""
	override fun handle(processor: IA32Processor) {
		processor.a.h = processor.flags.l
	}
}