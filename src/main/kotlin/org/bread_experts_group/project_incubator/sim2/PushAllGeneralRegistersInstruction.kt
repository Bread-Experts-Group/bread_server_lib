package org.bread_experts_group.project_incubator.sim2

object PushAllGeneralRegistersInstruction : Instruction {
	override fun execute(processor80386: Processor80386) {
		if (processor80386.operand32) {
			val temp = processor80386.ESP.qu
			processor80386.pushU32K(processor80386.EAX.qu)
			processor80386.pushU32K(processor80386.ECX.qu)
			processor80386.pushU32K(processor80386.EDX.qu)
			processor80386.pushU32K(processor80386.EBX.qu)
			processor80386.pushU32K(temp)
			processor80386.pushU32K(processor80386.EBP.qu)
			processor80386.pushU32K(processor80386.ESI.qu)
			processor80386.pushU32K(processor80386.EDI.qu)
			processor80386.logger.log(
				InstructionExecutionLogIdentifier(
					"PUSHAD"
				)
			)
		} else TODO("O16")
	}
}