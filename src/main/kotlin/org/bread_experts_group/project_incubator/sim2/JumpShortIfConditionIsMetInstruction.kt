package org.bread_experts_group.project_incubator.sim2

sealed class JumpShortIfConditionIsMetInstruction : Instruction {
	object NotCarry : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			val rel8 = processor80386.instructionReadS8()
			if (!processor80386.FLAGS.CARRY) {
				processor80386.EIP.qu += rel8.toUInt()
				if (!processor80386.operand32) processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
			}
			processor80386.logger.log(
				InstructionExecutionLogIdentifier(
					"JNC rel8",
					arrayOf(rel8, !processor80386.FLAGS.CARRY)
				)
			)
		}
	}

	object Carry : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			val rel8 = processor80386.instructionReadS8()
			if (processor80386.FLAGS.CARRY) {
				processor80386.EIP.qu += rel8.toUInt()
				if (!processor80386.operand32) processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
			}
			processor80386.logger.log(
				InstructionExecutionLogIdentifier(
					"JC rel8",
					arrayOf(rel8, processor80386.FLAGS.CARRY)
				)
			)
		}
	}

	object NotZero : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			val rel8 = processor80386.instructionReadS8()
			if (!processor80386.FLAGS.ZERO) {
				processor80386.EIP.qu += rel8.toUInt()
				if (!processor80386.operand32) processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
			}
			processor80386.logger.log(
				InstructionExecutionLogIdentifier(
					"JNZ rel8",
					arrayOf(rel8, !processor80386.FLAGS.ZERO)
				)
			)
		}
	}

	object Zero : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			val rel8 = processor80386.instructionReadS8()
			if (processor80386.FLAGS.ZERO) {
				processor80386.EIP.qu += rel8.toUInt()
				if (!processor80386.operand32) processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
			}
			processor80386.logger.log(
				InstructionExecutionLogIdentifier(
					"JZ rel8",
					arrayOf(rel8, processor80386.FLAGS.ZERO)
				)
			)
		}
	}

	object NotParity : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			val rel8 = processor80386.instructionReadS8()
			if (!processor80386.FLAGS.PARITY) {
				processor80386.EIP.qu += rel8.toUInt()
				if (!processor80386.operand32) processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
			}
			processor80386.logger.log(
				InstructionExecutionLogIdentifier(
					"JNP rel8",
					arrayOf(rel8, !processor80386.FLAGS.PARITY)
				)
			)
		}
	}

	object Parity : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			val rel8 = processor80386.instructionReadS8()
			if (processor80386.FLAGS.PARITY) {
				processor80386.EIP.qu += rel8.toUInt()
				if (!processor80386.operand32) processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
			}
			processor80386.logger.log(
				InstructionExecutionLogIdentifier(
					"JP rel8",
					arrayOf(rel8, processor80386.FLAGS.PARITY)
				)
			)
		}
	}

	object NotSign : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			val rel8 = processor80386.instructionReadS8()
			if (!processor80386.FLAGS.SIGN) {
				processor80386.EIP.qu += rel8.toUInt()
				if (!processor80386.operand32) processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
			}
			processor80386.logger.log(
				InstructionExecutionLogIdentifier(
					"JNS rel8",
					arrayOf(rel8, !processor80386.FLAGS.SIGN)
				)
			)
		}
	}

	object Sign : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			val rel8 = processor80386.instructionReadS8()
			if (processor80386.FLAGS.SIGN) {
				processor80386.EIP.qu += rel8.toUInt()
				if (!processor80386.operand32) processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
			}
			processor80386.logger.log(
				InstructionExecutionLogIdentifier(
					"JS rel8",
					arrayOf(rel8, processor80386.FLAGS.SIGN)
				)
			)
		}
	}

	object NotOverflow : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			val rel8 = processor80386.instructionReadS8()
			if (!processor80386.FLAGS.OVERFLOW) {
				processor80386.EIP.qu += rel8.toUInt()
				if (!processor80386.operand32) processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
			}
			processor80386.logger.log(
				InstructionExecutionLogIdentifier(
					"JNO rel8",
					arrayOf(rel8, !processor80386.FLAGS.OVERFLOW)
				)
			)
		}
	}

	object Overflow : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			val rel8 = processor80386.instructionReadS8()
			if (processor80386.FLAGS.OVERFLOW) {
				processor80386.EIP.qu += rel8.toUInt()
				if (!processor80386.operand32) processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
			}
			processor80386.logger.log(
				InstructionExecutionLogIdentifier(
					"JO rel8",
					arrayOf(rel8, processor80386.FLAGS.OVERFLOW)
				)
			)
		}
	}

	object NotAbove : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			val rel8 = processor80386.instructionReadS8()
			if (processor80386.FLAGS.CARRY || processor80386.FLAGS.ZERO) {
				processor80386.EIP.qu += rel8.toUInt()
				if (!processor80386.operand32) processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
			}
			processor80386.logger.log(
				InstructionExecutionLogIdentifier(
					"JNA rel8",
					arrayOf(rel8, processor80386.FLAGS.CARRY || processor80386.FLAGS.ZERO)
				)
			)
		}
	}

	object Above : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			val rel8 = processor80386.instructionReadS8()
			if (!(processor80386.FLAGS.CARRY || processor80386.FLAGS.ZERO)) {
				processor80386.EIP.qu += rel8.toUInt()
				if (!processor80386.operand32) processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
			}
			processor80386.logger.log(
				InstructionExecutionLogIdentifier(
					"JA rel8",
					arrayOf(rel8, !(processor80386.FLAGS.CARRY || processor80386.FLAGS.ZERO))
				)
			)
		}
	}

	object NotLess : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			val rel8 = processor80386.instructionReadS8()
			if (processor80386.FLAGS.SIGN == processor80386.FLAGS.OVERFLOW) {
				processor80386.EIP.qu += rel8.toUInt()
				if (!processor80386.operand32) processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
			}
			processor80386.logger.log(
				InstructionExecutionLogIdentifier(
					"JNL rel8",
					arrayOf(rel8, processor80386.FLAGS.SIGN == processor80386.FLAGS.OVERFLOW)
				)
			)
		}
	}

	object Less : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			val rel8 = processor80386.instructionReadS8()
			if (processor80386.FLAGS.SIGN != processor80386.FLAGS.OVERFLOW) {
				processor80386.EIP.qu += rel8.toUInt()
				if (!processor80386.operand32) processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
			}
			processor80386.logger.log(
				InstructionExecutionLogIdentifier(
					"JL rel8",
					arrayOf(rel8, processor80386.FLAGS.SIGN != processor80386.FLAGS.OVERFLOW)
				)
			)
		}
	}

	object LessOrEqual : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			val rel8 = processor80386.instructionReadS8()
			if (processor80386.FLAGS.ZERO && (processor80386.FLAGS.SIGN != processor80386.FLAGS.OVERFLOW)) {
				processor80386.EIP.qu += rel8.toUInt()
				if (!processor80386.operand32) processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
			}
			processor80386.logger.log(
				InstructionExecutionLogIdentifier(
					"JLE rel8",
					arrayOf(
						rel8,
						processor80386.FLAGS.ZERO && (processor80386.FLAGS.SIGN != processor80386.FLAGS.OVERFLOW)
					)
				)
			)
		}
	}

	object Greater : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			val rel8 = processor80386.instructionReadS8()
			if (!processor80386.FLAGS.ZERO && (processor80386.FLAGS.SIGN == processor80386.FLAGS.OVERFLOW)) {
				processor80386.EIP.qu += rel8.toUInt()
				if (!processor80386.operand32) processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
			}
			processor80386.logger.log(
				InstructionExecutionLogIdentifier(
					"JG rel8",
					arrayOf(
						rel8,
						!processor80386.FLAGS.ZERO && (processor80386.FLAGS.SIGN == processor80386.FLAGS.OVERFLOW)
					)
				)
			)
		}
	}

	object CXIs0 : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			val rel8 = processor80386.instructionReadS8()
			if (processor80386.address32) {
				if (processor80386.ECX.qu == 0u) {
					processor80386.EIP.qu += rel8.toUInt()
					if (!processor80386.operand32) processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JECXZ rel8",
						arrayOf(
							rel8,
							processor80386.ECX.qu == 0u
						)
					)
				)
			} else {
				if (processor80386.CX.du == 0u.toUShort()) {
					processor80386.EIP.qu += rel8.toUInt()
					if (!processor80386.operand32) processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JCXZ rel8",
						arrayOf(
							rel8,
							processor80386.CX.du == 0u.toUShort()
						)
					)
				)
			}
		}
	}
}