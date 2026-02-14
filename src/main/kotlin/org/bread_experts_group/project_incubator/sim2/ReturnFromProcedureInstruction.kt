package org.bread_experts_group.project_incubator.sim2

sealed class ReturnFromProcedureInstruction : Instruction {
	object Near : ReturnFromProcedureInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.operand32) {
				processor80386.EIP.qu = processor80386.popU32K()
			} else {
				processor80386.IP.du = processor80386.popU16K()
				processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
			}
			processor80386.logger.log(
				InstructionExecutionLogIdentifier(
					"RET"
				)
			)
		}
	}

	object Far : ReturnFromProcedureInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (
				!processor80386.CR0.PROTECTION_ENABLE ||
				(processor80386.CR0.PROTECTION_ENABLE && processor80386.EFLAGS.VIRTUAL_8086_MODE)
			) {
				if (processor80386.operand32) {
					processor80386.EIP.qu = processor80386.popU32K()
					processor80386.CS.du = processor80386.popU32K().toUShort()
					processor80386.CS.base = processor80386.CS.du.toUInt() shl 4
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"RETF"
						)
					)
				} else {
					processor80386.IP.du = processor80386.popU16K()
					processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
					processor80386.CS.du = processor80386.popU16K()
					processor80386.CS.base = processor80386.CS.du.toUInt() shl 4
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"RETF"
						)
					)
				}
			} else TODO("PROTECTED")
		}
	}
}