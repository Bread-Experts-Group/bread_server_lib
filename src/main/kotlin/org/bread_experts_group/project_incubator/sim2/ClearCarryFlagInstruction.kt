package org.bread_experts_group.project_incubator.sim2

object ClearCarryFlagInstruction : Instruction {
	override fun execute(processor80386: Processor80386) {
		processor80386.logger.log(
			InstructionExecutionLogIdentifier(
				"CLC",
				effects = if (processor80386.FLAGS.CARRY) arrayOf("CF=false") else emptyArray()
			)
		)
		processor80386.FLAGS.CARRY = false
	}
}