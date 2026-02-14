package org.bread_experts_group.project_incubator.sim2

sealed class StoreStringDataInstruction : Instruction {
	object Bit8 : StoreStringDataInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.address32) {
				processor80386.memorySink.writeS8(processor80386.ES.base + processor80386.EDI.qu, processor80386.AL.b)
				if (processor80386.FLAGS.DIRECTION) processor80386.EDI.qu--
				else processor80386.EDI.qu++
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"STOSB",
						effects = arrayOf(
							"EDI=${processor80386.EDI.qu}u",
						)
					)
				)
			} else {
				processor80386.memorySink.writeS8(processor80386.ES.base + processor80386.DI.du, processor80386.AL.b)
				if (processor80386.FLAGS.DIRECTION) processor80386.DI.du--
				else processor80386.DI.du++
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"STOSB",
						effects = arrayOf(
							"DI=${processor80386.DI.du}u",
						)
					)
				)
			}
		}
	}

	object Bit1632 : StoreStringDataInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.address32) {
				if (processor80386.operand32) TODO("A32 O32")
				else TODO("A32 O16")
			} else {
				if (processor80386.operand32) {
					processor80386.memorySink.writeS32(
						processor80386.ES.base + processor80386.DI.du,
						processor80386.EAX.q
					)
					if (processor80386.FLAGS.DIRECTION) processor80386.DI.du = (processor80386.DI.du - 4u).toUShort()
					else processor80386.DI.du = (processor80386.DI.du + 4u).toUShort()
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"STOSD",
							effects = arrayOf(
								"DI=${processor80386.DI.du}u",
							)
						)
					)
				} else {
					processor80386.memorySink.writeS16(
						processor80386.ES.base + processor80386.DI.du,
						processor80386.AX.d
					)
					if (processor80386.FLAGS.DIRECTION) processor80386.DI.du = (processor80386.DI.du - 2u).toUShort()
					else processor80386.DI.du = (processor80386.DI.du + 2u).toUShort()
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"STOSW",
							effects = arrayOf(
								"DI=${processor80386.DI.du}u",
							)
						)
					)
				}
			}
		}
	}
}