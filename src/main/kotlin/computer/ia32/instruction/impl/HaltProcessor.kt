package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import java.util.concurrent.CountDownLatch

class HaltProcessor : Instruction(0xF4u, "hlt") {
	override fun operands(processor: IA32Processor): String = ""
	override fun handle(processor: IA32Processor) {
		processor.halt = CountDownLatch(1)
	}
}