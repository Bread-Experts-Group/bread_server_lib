package org.bread_experts_group.project_incubator.sim2

sealed class LoadStringOperandInstruction : Instruction {
	object Bit8 : LoadStringOperandInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.address32) TODO("A32")
			else {
				val src = (processor80386.segmentOverride ?: processor80386.DS).base + processor80386.SI.du
				processor80386.AL.bu = processor80386.memorySource.readU8K(src)
				if (!processor80386.FLAGS.DIRECTION) processor80386.SI.du++
				else processor80386.SI.du--
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"LODSB",
						arrayOf(processor80386.AL.bu, src),
						arrayOf(
							"AL=${processor80386.AL.bu}u",
							"SI=${processor80386.SI.du}u",
							"ZF=${processor80386.FLAGS.ZERO}",
							"SF=${processor80386.FLAGS.SIGN}",
							"PF=${processor80386.FLAGS.PARITY}",
							"CF=${processor80386.FLAGS.CARRY}",
							"AF=${processor80386.FLAGS.ADJUST}",
							"OF=${processor80386.FLAGS.OVERFLOW}",
						)
					)
				)
			}
		}
	}

	object Bit1632 : LoadStringOperandInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.address32) TODO("A32")
			else {
				val src = (processor80386.segmentOverride ?: processor80386.DS).base + processor80386.SI.du
				if (processor80386.operand32) {
					processor80386.EAX.qu = processor80386.memorySource.readU32K(src)
					if (!processor80386.FLAGS.DIRECTION) processor80386.SI.du = (processor80386.SI.du + 4u).toUShort()
					else processor80386.SI.du = (processor80386.SI.du - 4u).toUShort()
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"LODSD",
							arrayOf(processor80386.EAX.qu, src),
							arrayOf(
								"AX=${processor80386.AX.du}u",
								"SI=${processor80386.SI.du}u",
								"ZF=${processor80386.FLAGS.ZERO}",
								"SF=${processor80386.FLAGS.SIGN}",
								"PF=${processor80386.FLAGS.PARITY}",
								"CF=${processor80386.FLAGS.CARRY}",
								"AF=${processor80386.FLAGS.ADJUST}",
								"OF=${processor80386.FLAGS.OVERFLOW}",
							)
						)
					)
				} else {
					processor80386.AX.du = processor80386.memorySource.readU16K(src)
					if (!processor80386.FLAGS.DIRECTION) processor80386.SI.du = (processor80386.SI.du + 2u).toUShort()
					else processor80386.SI.du = (processor80386.SI.du - 2u).toUShort()
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"LODSW",
							arrayOf(processor80386.AX.du, src),
							arrayOf(
								"AX=${processor80386.AX.du}u",
								"SI=${processor80386.SI.du}u",
								"ZF=${processor80386.FLAGS.ZERO}",
								"SF=${processor80386.FLAGS.SIGN}",
								"PF=${processor80386.FLAGS.PARITY}",
								"CF=${processor80386.FLAGS.CARRY}",
								"AF=${processor80386.FLAGS.ADJUST}",
								"OF=${processor80386.FLAGS.OVERFLOW}",
							)
						)
					)
				}
			}
		}
	}
}