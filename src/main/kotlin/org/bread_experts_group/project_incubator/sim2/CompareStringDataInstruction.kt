package org.bread_experts_group.project_incubator.sim2

sealed class CompareStringDataInstruction : Instruction {
	object Bit8 : CompareStringOperandsInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.address32) TODO("A32")
			else {
				val src = processor80386.ES.base + processor80386.DI.du
				val srcV = processor80386.memorySource.readU8K(src)
				val result = computeBorrow8(processor80386, processor80386.AL.bu, srcV)
				computeZFPFSF8(processor80386, result)
				if (!processor80386.FLAGS.DIRECTION) processor80386.DI.du++
				else processor80386.DI.du--
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"SCASB",
						arrayOf(processor80386.AL.bu, srcV),
						arrayOf(
							"DI=${processor80386.DI.du}u",
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

	object Bit1632 : CompareStringOperandsInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.address32) TODO("A32")
			else {
				val src = processor80386.ES.base + processor80386.DI.du
				if (processor80386.operand32) {
					val srcV = processor80386.memorySource.readU32K(src)
					val result = computeBorrow32(processor80386, processor80386.EAX.qu, srcV)
					computeZFPFSF32(processor80386, result)
					if (!processor80386.FLAGS.DIRECTION) processor80386.DI.du = (processor80386.DI.du + 4u).toUShort()
					else processor80386.DI.du = (processor80386.DI.du - 4u).toUShort()
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"SCASD",
							arrayOf(processor80386.EAX.qu, srcV),
							arrayOf(
								"DI=${processor80386.DI.du}u",
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
					val srcV = processor80386.memorySource.readU16K(src)
					val result = computeBorrow16(processor80386, processor80386.AX.du, srcV)
					computeZFPFSF16(processor80386, result)
					if (!processor80386.FLAGS.DIRECTION) processor80386.DI.du = (processor80386.DI.du + 2u).toUShort()
					else processor80386.DI.du = (processor80386.DI.du - 2u).toUShort()
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"SCASW",
							arrayOf(processor80386.AX.du, srcV),
							arrayOf(
								"DI=${processor80386.DI.du}u",
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