package org.bread_experts_group.project_incubator.sim2

sealed class CallProcedureInstruction : Instruction {
	object Near : CallProcedureInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.operand32) {
				val rel32 = processor80386.instructionReadS32()
				processor80386.pushU32K(processor80386.EIP.qu)
				processor80386.EIP.qu += rel32.toUInt()
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"CALL rel32",
						arrayOf(rel32)
					)
				)
			} else {
				val rel16 = processor80386.instructionReadS16()
				processor80386.pushU16K(processor80386.IP.du)
				processor80386.EIP.qu = (processor80386.EIP.qu + rel16.toUInt()) and 0x0000FFFFu
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"CALL rel16",
						arrayOf(rel16)
					)
				)
			}
		}
	}

	class NearIndirect(val mod: Int, val rm: Int) : CallProcedureInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.address32) {
				if (processor80386.operand32) TODO("a32 o32")
				else TODO("a32 o16")
			} else {
				val (sAddress, sRegister) = modRm16Addressing(processor80386, mod, rm)
				if (processor80386.operand32) {
					if (sAddress != null) TODO("ADDR")
					else {
						processor80386.pushU32K(processor80386.EIP.qu)
						processor80386.EIP.qu = (sRegister as Register32).qu
						processor80386.logger.log(
							InstructionExecutionLogIdentifier(
								"CALL r/m32",
								arrayOf(sRegister.label)
							)
						)
					}
				} else {
					if (sAddress != null) TODO("ADDR")
					else {
						processor80386.pushU16K(processor80386.IP.du)
						processor80386.EIP.qu = (sRegister as Register16).du.toUInt()
						processor80386.logger.log(
							InstructionExecutionLogIdentifier(
								"CALL r/m16",
								arrayOf(sRegister.label)
							)
						)
					}
				}
			}
		}
	}

	object Gate : CallProcedureInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (
				!processor80386.CR0.PROTECTION_ENABLE ||
				(processor80386.CR0.PROTECTION_ENABLE && processor80386.EFLAGS.VIRTUAL_8086_MODE)
			) {
				if (processor80386.operand32) {
					val eip = processor80386.instructionReadU32K()
					val cs = processor80386.instructionReadU16K()
					processor80386.pushU32K(processor80386.CS.du.toUInt())
					processor80386.pushU32K(processor80386.EIP.qu)
					processor80386.CS.du = cs
					processor80386.EIP.qu = eip
					processor80386.CS.base = processor80386.CS.du.toUInt() shl 4
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"CALL ptr16:32",
							arrayOf()
						)
					)
				} else {
					val ip = processor80386.instructionReadU16K()
					val cs = processor80386.instructionReadU16K()
					processor80386.pushU16K(processor80386.CS.du)
					processor80386.pushU16K(processor80386.IP.du)
					processor80386.CS.du = cs
					processor80386.IP.du = ip
					processor80386.CS.base = processor80386.CS.du.toUInt() shl 4
					processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"CALL ptr16:16",
							arrayOf()
						)
					)
				}
			} else TODO("Protected")
		}
	}

	class GateIndirect(val mod: Int, val rm: Int) : CallProcedureInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (
				!processor80386.CR0.PROTECTION_ENABLE ||
				(processor80386.CR0.PROTECTION_ENABLE && processor80386.EFLAGS.VIRTUAL_8086_MODE)
			) {
				if (processor80386.address32) {
					if (processor80386.operand32) TODO("A32 O32")
					else TODO("A32 O16")
				} else {
					val (sAddress, sRegister) = modRm16Addressing(processor80386, mod, rm)
					if (sRegister != null) TODO("Reg?")
					if (processor80386.operand32) {
						val eip = processor80386.memorySource.readU32K(sAddress!!)
						val cs = processor80386.memorySource.readU16K(sAddress + 4u)
						processor80386.pushU32K(processor80386.CS.du.toUInt())
						processor80386.pushU32K(processor80386.EIP.qu)
						processor80386.CS.du = cs
						processor80386.EIP.qu = eip
						processor80386.CS.base = processor80386.CS.du.toUInt() shl 4
						processor80386.logger.log(
							InstructionExecutionLogIdentifier(
								"CALL m16:32",
								arrayOf()
							)
						)
					} else {
						val ip = processor80386.memorySource.readU16K(sAddress!!)
						val cs = processor80386.memorySource.readU16K(sAddress + 2u)
						processor80386.pushU16K(processor80386.CS.du)
						processor80386.pushU16K(processor80386.IP.du)
						processor80386.CS.du = cs
						processor80386.IP.du = ip
						processor80386.CS.base = processor80386.CS.du.toUInt() shl 4
						processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
						processor80386.logger.log(
							InstructionExecutionLogIdentifier(
								"CALL m16:16",
								arrayOf()
							)
						)
					}
				}
			} else TODO("Protected")
		}
	}
}