package org.bread_experts_group.api.computer.ia32.instruction.impl

import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.type.Instruction

class InterruptReturn : Instruction(0xCFu, "iret") {
	companion object {
		val BIOS_RETURN = InterruptReturn()
	}

	override fun operands(processor: IA32Processor): String = ""
	override fun handle(processor: IA32Processor) {
		if (processor.realMode()) {
			processor.ip.tex = processor.pop16().toUInt()
			processor.cs.tx = processor.pop16()
			processor.flags.tx = processor.pop16()
		} else TODO("PROTECTED IRET")
	}
}