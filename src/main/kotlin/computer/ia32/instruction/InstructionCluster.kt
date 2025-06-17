package org.bread_experts_group.computer.ia32.instruction

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.type.Instruction

/**
 * Defines a cluster of [Instruction]s, contained within a class.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
interface InstructionCluster {
	fun getInstructions(processor: IA32Processor): List<Instruction>
}