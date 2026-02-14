package org.bread_experts_group.project_incubator.sim2

sealed class ExchangeRMWithRegisterInstruction : Instruction {
	object Bit1632 : ExchangeRMWithRegisterInstruction() {
		override fun execute(processor80386: Processor80386) {
			val (mod, reg, rm) = decompose233(processor80386.instructionReadS8())
			if (processor80386.address32) {
				if (processor80386.operand32) TODO("A32 O32")
				else TODO("A32 O16")
			} else {
				val (nAddress, nRegister) = modRm16Addressing(processor80386, mod, rm)
				if (processor80386.operand32) {
					val tRegister = rd(processor80386, reg)
					if (nAddress != null) TODO("Addr")
					else {
						val temp = tRegister.qu
						tRegister.qu = (nRegister as Register32).qu
						nRegister.qu = temp
						processor80386.logger.log(
							InstructionExecutionLogIdentifier(
								"XCHG r/m32,r32",
								arrayOf(nRegister.label, tRegister.label),
							)
						)
					}
				} else {
					val tRegister = rw(processor80386, reg)
					if (nAddress != null) TODO("Addr")
					else {
						val temp = tRegister.du
						tRegister.du = (nRegister as Register16).du
						nRegister.du = temp
						processor80386.logger.log(
							InstructionExecutionLogIdentifier(
								"XCHG r/m16,r16",
								arrayOf(nRegister.label, tRegister.label),
							)
						)
					}
				}
			}
		}
	}
}