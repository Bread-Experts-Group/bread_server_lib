package org.bread_experts_group.project_incubator.sim2

sealed class OutputToPortInstruction : Instruction {
	object DXAL : OutputToPortInstruction() {
		override fun execute(processor80386: Processor80386) {
			println("OUTPUT ${processor80386.AL.b} TO ${processor80386.DX.d} !")
			processor80386.logger.log(
				InstructionExecutionLogIdentifier(
					"OUT DX,AL",
					arrayOf(processor80386.DX.d, processor80386.AL.b)
				)
			)
		}
	}
}