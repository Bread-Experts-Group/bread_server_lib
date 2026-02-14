package org.bread_experts_group.project_incubator.sim2

object SetCarryFlagInstruction : Instruction {
	override fun execute(processor80386: Processor80386) {
		processor80386.logger.log(
			InstructionExecutionLogIdentifier(
				"STC",
				effects = if (!processor80386.FLAGS.CARRY) arrayOf("CF=true") else emptyArray()
			)
		)
		processor80386.FLAGS.CARRY = true
	}
}