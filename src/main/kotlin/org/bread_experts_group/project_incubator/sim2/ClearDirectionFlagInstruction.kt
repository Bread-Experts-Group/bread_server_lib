package org.bread_experts_group.project_incubator.sim2

object ClearDirectionFlagInstruction : Instruction {
	override fun execute(processor80386: Processor80386) {
		processor80386.logger.log(
			InstructionExecutionLogIdentifier(
				"CLD",
				effects = if (processor80386.FLAGS.DIRECTION) arrayOf("DF=false") else emptyArray()
			)
		)
		processor80386.FLAGS.DIRECTION = false
	}
}