package org.bread_experts_group.project_incubator.sim2

object SetDirectionFlagInstruction : Instruction {
	override fun execute(processor80386: Processor80386) {
		processor80386.logger.log(
			InstructionExecutionLogIdentifier(
				"STD",
				effects = if (!processor80386.FLAGS.DIRECTION) arrayOf("DF=true") else emptyArray()
			)
		)
		processor80386.FLAGS.DIRECTION = true
	}
}