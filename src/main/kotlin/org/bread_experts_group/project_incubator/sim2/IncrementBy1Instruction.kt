package org.bread_experts_group.project_incubator.sim2

sealed class IncrementBy1Instruction : Instruction {
	class Register(private val register: Int) : IncrementBy1Instruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.operand32) {
				val register = rd(processor80386, register)
				val result = computeCarry32(processor80386, register.qu, 1u, false)
				computeZFPFSF32(processor80386, result)
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"INC r32",
						arrayOf(register.label to register.qu),
						arrayOf(
							"${register.label}=$result",
							"OF=${processor80386.FLAGS.OVERFLOW}",
							"AF=${processor80386.FLAGS.ADJUST}",
							"ZF=${processor80386.FLAGS.ZERO}",
							"SF=${processor80386.FLAGS.SIGN}",
							"PF=${processor80386.FLAGS.PARITY}"
						)
					)
				)
				register.qu = result
			} else {
				val register = rw(processor80386, register)
				val result = computeCarry16(processor80386, register.du, 1u, false)
				computeZFPFSF16(processor80386, result)
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"INC r16",
						arrayOf(register.label to register.du),
						arrayOf(
							"${register.label}=$result",
							"OF=${processor80386.FLAGS.OVERFLOW}",
							"AF=${processor80386.FLAGS.ADJUST}",
							"ZF=${processor80386.FLAGS.ZERO}",
							"SF=${processor80386.FLAGS.SIGN}",
							"PF=${processor80386.FLAGS.PARITY}"
						)
					)
				)
				register.du = result
			}
		}
	}
}