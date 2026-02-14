package org.bread_experts_group.project_incubator.sim2

class LoadInterruptDescriptorTableRegister(val mod: Int, val rm: Int) : Instruction {
	override fun execute(processor80386: Processor80386) {
		if (processor80386.address32) {
			if (processor80386.operand32) TODO("O32")
			else TODO("O16")
		} else {
			val (sAddress, sRegister) = modRm16Addressing(processor80386, mod, rm)
			if (sRegister != null) TODO("reg?")
			if (processor80386.operand32) {
				processor80386.IDTR_LIMIT.du = processor80386.memorySource.readU16K(sAddress!!)
				processor80386.IDTR_BASE.qu = processor80386.memorySource.readU32K(sAddress + 2u)
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"LIDT"
					)
				)
			} else TODO("O16")
		}
	}
}