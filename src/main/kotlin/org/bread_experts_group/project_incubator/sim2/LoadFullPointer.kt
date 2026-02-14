package org.bread_experts_group.project_incubator.sim2

sealed class LoadFullPointer : Instruction {
	class Basic(val segment: Processor80386.SegmentRegister, val c: Char) : LoadFullPointer() {
		override fun execute(processor80386: Processor80386) {
			val (mod, reg, rm) = decompose233(processor80386.instructionReadS8())
			if (processor80386.address32) {
				if (processor80386.operand32) TODO("A32 O32")
				else TODO("A32 O16")
			} else {
				val (sAddress, sRegister) = modRm16Addressing(processor80386, mod, rm)
				if (sRegister != null) TODO("Reg?")
				if (processor80386.operand32) {
					val dRegister = rd(processor80386, reg)
					dRegister.q = processor80386.memorySource.readS32(sAddress!!)
					segment.d = processor80386.memorySource.readS16(sAddress + 4u)
					segment.base = segment.du.toUInt() shl 4
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"L${c}S r32,m16:32",
							arrayOf()
						)
					)
				} else {
					val dRegister = rw(processor80386, reg)
					dRegister.d = processor80386.memorySource.readS16(sAddress!!)
					segment.d = processor80386.memorySource.readS16(sAddress + 2u)
					segment.base = segment.du.toUInt() shl 4
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"L${c}S r16,m16:16",
							arrayOf()
						)
					)
				}
			}
		}
	}
}