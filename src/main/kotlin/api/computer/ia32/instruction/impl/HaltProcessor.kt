package org.bread_experts_group.api.computer.ia32.instruction.impl

import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.assembler.Assembler
import org.bread_experts_group.api.computer.ia32.instruction.AssembledInstruction
import org.bread_experts_group.api.computer.ia32.instruction.type.Instruction
import java.io.OutputStream
import java.util.concurrent.CountDownLatch

class HaltProcessor : Instruction(0xF4u, "hlt"), AssembledInstruction {
	override fun operands(processor: IA32Processor): String = ""
	override fun handle(processor: IA32Processor) {
		processor.halt = CountDownLatch(1)
	}

	override val arguments: Int = 0
	override fun acceptable(assembler: Assembler, from: ArrayDeque<String>): Boolean = true
	override fun produce(assembler: Assembler, into: OutputStream, from: ArrayDeque<String>) {
		into.write(this.opcode.toInt())
	}
}