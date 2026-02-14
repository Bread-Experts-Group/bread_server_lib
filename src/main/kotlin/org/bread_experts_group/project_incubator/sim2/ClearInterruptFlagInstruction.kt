package org.bread_experts_group.project_incubator.sim2

object ClearInterruptFlagInstruction : Instruction {
	override fun execute(processor80386: Processor80386) {
		if (!processor80386.CR0.PROTECTION_ENABLE) {
			processor80386.logger.log(
				InstructionExecutionLogIdentifier(
					"CLI",
					effects = if (processor80386.FLAGS.INTERRUPT_ENABLE) arrayOf("IF=false") else emptyArray()
				)
			)
			processor80386.FLAGS.INTERRUPT_ENABLE = false
		} else TODO("Protected mode")
	}
}