package org.bread_experts_group.project_incubator.sim2

object PopAllGeneralRegistersInstruction : Instruction {
	override fun execute(processor80386: Processor80386) {
		if (processor80386.operand32) {
			processor80386.EDI.qu = processor80386.popU32K()
			processor80386.ESI.qu = processor80386.popU32K()
			processor80386.EBP.qu = processor80386.popU32K()
			processor80386.popU32K()
			processor80386.EBX.qu = processor80386.popU32K()
			processor80386.EDX.qu = processor80386.popU32K()
			processor80386.ECX.qu = processor80386.popU32K()
			processor80386.EAX.qu = processor80386.popU32K()
			processor80386.logger.log(
				InstructionExecutionLogIdentifier(
					"POPAD"
				)
			)
		} else TODO("O16")
	}
}