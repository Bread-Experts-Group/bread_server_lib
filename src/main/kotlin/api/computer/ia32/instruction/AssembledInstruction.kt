package org.bread_experts_group.api.computer.ia32.instruction

import org.bread_experts_group.api.computer.ia32.assembler.Assembler
import java.io.OutputStream

interface AssembledInstruction {
	val arguments: Int
	fun acceptable(assembler: Assembler, from: ArrayDeque<String>): Boolean
	fun produce(assembler: Assembler, into: OutputStream, from: ArrayDeque<String>)
}