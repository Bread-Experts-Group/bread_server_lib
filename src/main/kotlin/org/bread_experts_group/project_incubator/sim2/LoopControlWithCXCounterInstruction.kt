package org.bread_experts_group.project_incubator.sim2

sealed class LoopControlWithCXCounterInstruction : Instruction {
	object Unconditional : LoopControlWithCXCounterInstruction() {
		override fun execute(processor80386: Processor80386) {
			val rel8 = processor80386.instructionReadS8()
			if (processor80386.address32) {
				processor80386.ECX.qu--
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"LOOP rel8",
						arrayOf(rel8),
						arrayOf("ECX=${processor80386.ECX.qu}")
					)
				)
				if (processor80386.ECX.qu == 0u) return
				if (processor80386.operand32) {
					processor80386.EIP.qu += rel8.toUInt()
				} else {
					processor80386.IP.du = (processor80386.IP.du + rel8.toUShort()).toUShort()
				}
			} else {
				processor80386.CX.du--
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"LOOP rel8",
						arrayOf(rel8),
						arrayOf("CX=${processor80386.CX.du}")
					)
				)
				if (processor80386.CX.du == 0u.toUShort()) return
				if (processor80386.operand32) {
					processor80386.EIP.qu += rel8.toUInt()
				} else {
					processor80386.IP.du = (processor80386.IP.du + rel8.toUShort()).toUShort()
				}
			}
		}
	}

	object Zero : LoopControlWithCXCounterInstruction() {
		override fun execute(processor80386: Processor80386) {
			val rel8 = processor80386.instructionReadS8()
			if (processor80386.address32) {
				processor80386.ECX.qu--
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"LOOPZ rel8",
						arrayOf(rel8),
						arrayOf("ECX=${processor80386.ECX.qu}")
					)
				)
				if (processor80386.ECX.qu == 0u || !processor80386.FLAGS.ZERO) return
				if (processor80386.operand32) {
					processor80386.EIP.qu += rel8.toUInt()
				} else {
					processor80386.IP.du = (processor80386.IP.du + rel8.toUShort()).toUShort()
				}
			} else {
				processor80386.CX.du--
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"LOOPZ rel8",
						arrayOf(rel8),
						arrayOf("CX=${processor80386.CX.du}")
					)
				)
				if (processor80386.CX.du == 0u.toUShort() || !processor80386.FLAGS.ZERO) return
				if (processor80386.operand32) {
					processor80386.EIP.qu += rel8.toUInt()
				} else {
					processor80386.IP.du = (processor80386.IP.du + rel8.toUShort()).toUShort()
				}
			}
		}
	}

	object NotZero : LoopControlWithCXCounterInstruction() {
		override fun execute(processor80386: Processor80386) {
			val rel8 = processor80386.instructionReadS8()
			if (processor80386.address32) {
				processor80386.ECX.qu--
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"LOOPNZ rel8",
						arrayOf(rel8),
						arrayOf("ECX=${processor80386.ECX.qu}")
					)
				)
				if (processor80386.ECX.qu == 0u || processor80386.FLAGS.ZERO) return
				if (processor80386.operand32) {
					processor80386.EIP.qu += rel8.toUInt()
				} else {
					processor80386.IP.du = (processor80386.IP.du + rel8.toUShort()).toUShort()
				}
			} else {
				processor80386.CX.du--
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"LOOPNZ rel8",
						arrayOf(rel8),
						arrayOf("CX=${processor80386.CX.du}")
					)
				)
				if (processor80386.CX.du == 0u.toUShort() || processor80386.FLAGS.ZERO) return
				if (processor80386.operand32) {
					processor80386.EIP.qu += rel8.toUInt()
				} else {
					processor80386.IP.du = (processor80386.IP.du + rel8.toUShort()).toUShort()
				}
			}
		}
	}
}