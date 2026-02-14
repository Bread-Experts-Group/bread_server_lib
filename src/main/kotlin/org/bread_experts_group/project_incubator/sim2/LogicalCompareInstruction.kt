package org.bread_experts_group.project_incubator.sim2

sealed class LogicalCompareInstruction : Instruction {
	object ALImm8 : LogicalCompareInstruction() {
		override fun execute(processor80386: Processor80386) {
			val imm8 = processor80386.instructionReadU8K()
			val result = processor80386.AL.bu and imm8
			processor80386.FLAGS.CARRY = false
			processor80386.FLAGS.OVERFLOW = false
			computeZFPFSF8(processor80386, result)
			processor80386.logger.log(
				InstructionExecutionLogIdentifier(
					"TEST AL,imm8",
					arrayOf(processor80386.AL.bu, imm8),
					arrayOf(
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

	object EAXImm1632 : LogicalCompareInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.operand32) {
				val imm32 = processor80386.instructionReadU32K()
				val result = processor80386.EAX.qu and imm32
				processor80386.FLAGS.CARRY = false
				processor80386.FLAGS.OVERFLOW = false
				computeZFPFSF32(processor80386, result)
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"TEST EAX,imm32",
						arrayOf(processor80386.EAX.qu, imm32),
						arrayOf(
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
				TODO("Test O16")
			}
		}
	}
}