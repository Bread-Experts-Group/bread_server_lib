package org.bread_experts_group.computer.ia32.instruction

import org.bread_experts_group.computer.ia32.assembler.Assembler
import java.io.OutputStream
import java.util.logging.Logger

interface AssembledInstruction {
	val arguments: Int
	fun acceptable(logger: Logger, from: ArrayDeque<String>): Boolean
	fun produce(logger: Logger, mode: Assembler.BitMode, into: OutputStream, from: ArrayDeque<String>)
}