package org.bread_experts_group.project_incubator.sim2

sealed class JumpNearIfConditionIsMet {
	object NotCarry : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.operand32) {
				val rel32 = processor80386.instructionReadS32()
				if (!processor80386.FLAGS.CARRY) {
					processor80386.EIP.qu += rel32.toUInt()
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JNC rel32",
						arrayOf(rel32, !processor80386.FLAGS.CARRY)
					)
				)
			} else {
				val rel16 = processor80386.instructionReadS16()
				if (!processor80386.FLAGS.CARRY) {
					processor80386.EIP.qu += rel16.toUInt()
					processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JNC rel16",
						arrayOf(rel16, !processor80386.FLAGS.CARRY)
					)
				)
			}
		}
	}

	object Carry : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.operand32) {
				val rel32 = processor80386.instructionReadS32()
				if (processor80386.FLAGS.CARRY) {
					processor80386.EIP.qu += rel32.toUInt()
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JC rel32",
						arrayOf(rel32, processor80386.FLAGS.CARRY)
					)
				)
			} else {
				val rel16 = processor80386.instructionReadS16()
				if (processor80386.FLAGS.CARRY) {
					processor80386.EIP.qu += rel16.toUInt()
					processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JC rel16",
						arrayOf(rel16, processor80386.FLAGS.CARRY)
					)
				)
			}
		}
	}

	object NotZero : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.operand32) {
				val rel32 = processor80386.instructionReadS32()
				if (!processor80386.FLAGS.ZERO) {
					processor80386.EIP.qu += rel32.toUInt()
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JNZ rel32",
						arrayOf(rel32, !processor80386.FLAGS.ZERO)
					)
				)
			} else {
				val rel16 = processor80386.instructionReadS16()
				if (!processor80386.FLAGS.ZERO) {
					processor80386.EIP.qu += rel16.toUInt()
					processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JNZ rel16",
						arrayOf(rel16, !processor80386.FLAGS.ZERO)
					)
				)
			}
		}
	}

	object Zero : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.operand32) {
				val rel32 = processor80386.instructionReadS32()
				if (processor80386.FLAGS.ZERO) {
					processor80386.EIP.qu += rel32.toUInt()
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JZ rel32",
						arrayOf(rel32, processor80386.FLAGS.ZERO)
					)
				)
			} else {
				val rel16 = processor80386.instructionReadS16()
				if (processor80386.FLAGS.ZERO) {
					processor80386.EIP.qu += rel16.toUInt()
					processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JZ rel16",
						arrayOf(rel16, processor80386.FLAGS.ZERO)
					)
				)
			}
		}
	}

	object NotParity : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.operand32) {
				val rel32 = processor80386.instructionReadS32()
				if (!processor80386.FLAGS.PARITY) {
					processor80386.EIP.qu += rel32.toUInt()
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JNP rel32",
						arrayOf(rel32, !processor80386.FLAGS.PARITY)
					)
				)
			} else {
				val rel16 = processor80386.instructionReadS16()
				if (!processor80386.FLAGS.PARITY) {
					processor80386.EIP.qu += rel16.toUInt()
					processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JNP rel16",
						arrayOf(rel16, !processor80386.FLAGS.PARITY)
					)
				)
			}
		}
	}

	object Parity : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.operand32) {
				val rel32 = processor80386.instructionReadS32()
				if (processor80386.FLAGS.PARITY) {
					processor80386.EIP.qu += rel32.toUInt()
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JP rel32",
						arrayOf(rel32, processor80386.FLAGS.PARITY)
					)
				)
			} else {
				val rel16 = processor80386.instructionReadS16()
				if (processor80386.FLAGS.PARITY) {
					processor80386.EIP.qu += rel16.toUInt()
					processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JP rel16",
						arrayOf(rel16, processor80386.FLAGS.PARITY)
					)
				)
			}
		}
	}

	object NotSign : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.operand32) {
				val rel32 = processor80386.instructionReadS32()
				if (!processor80386.FLAGS.SIGN) {
					processor80386.EIP.qu += rel32.toUInt()
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JNS rel32",
						arrayOf(rel32, !processor80386.FLAGS.SIGN)
					)
				)
			} else {
				val rel16 = processor80386.instructionReadS16()
				if (!processor80386.FLAGS.SIGN) {
					processor80386.EIP.qu += rel16.toUInt()
					processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JNS rel16",
						arrayOf(rel16, !processor80386.FLAGS.SIGN)
					)
				)
			}
		}
	}

	object Sign : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.operand32) {
				val rel32 = processor80386.instructionReadS32()
				if (processor80386.FLAGS.SIGN) {
					processor80386.EIP.qu += rel32.toUInt()
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JS rel32",
						arrayOf(rel32, processor80386.FLAGS.SIGN)
					)
				)
			} else {
				val rel16 = processor80386.instructionReadS16()
				if (processor80386.FLAGS.SIGN) {
					processor80386.EIP.qu += rel16.toUInt()
					processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JS rel16",
						arrayOf(rel16, processor80386.FLAGS.SIGN)
					)
				)
			}
		}
	}

	object NotAbove : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.operand32) {
				val rel32 = processor80386.instructionReadS32()
				if (processor80386.FLAGS.CARRY || processor80386.FLAGS.ZERO) {
					processor80386.EIP.qu += rel32.toUInt()
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JNA rel32",
						arrayOf(rel32, processor80386.FLAGS.CARRY || processor80386.FLAGS.ZERO)
					)
				)
			} else {
				val rel16 = processor80386.instructionReadS16()
				if (processor80386.FLAGS.CARRY || processor80386.FLAGS.ZERO) {
					processor80386.EIP.qu += rel16.toUInt()
					processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JNA rel16",
						arrayOf(rel16, processor80386.FLAGS.CARRY || processor80386.FLAGS.ZERO)
					)
				)
			}
		}
	}

	object Above : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.operand32) {
				val rel32 = processor80386.instructionReadS32()
				if (!(processor80386.FLAGS.CARRY || processor80386.FLAGS.ZERO)) {
					processor80386.EIP.qu += rel32.toUInt()
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JA rel32",
						arrayOf(rel32, !(processor80386.FLAGS.CARRY || processor80386.FLAGS.ZERO))
					)
				)
			} else {
				val rel16 = processor80386.instructionReadS16()
				if (!(processor80386.FLAGS.CARRY || processor80386.FLAGS.ZERO)) {
					processor80386.EIP.qu += rel16.toUInt()
					processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JA rel16",
						arrayOf(rel16, !(processor80386.FLAGS.CARRY || processor80386.FLAGS.ZERO))
					)
				)
			}
		}
	}

	object NotOverflow : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.operand32) {
				val rel32 = processor80386.instructionReadS32()
				if (!processor80386.FLAGS.OVERFLOW) {
					processor80386.EIP.qu += rel32.toUInt()
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JNO rel32",
						arrayOf(rel32, !processor80386.FLAGS.OVERFLOW)
					)
				)
			} else {
				val rel16 = processor80386.instructionReadS16()
				if (!processor80386.FLAGS.OVERFLOW) {
					processor80386.EIP.qu += rel16.toUInt()
					processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JNO rel16",
						arrayOf(rel16, !processor80386.FLAGS.OVERFLOW)
					)
				)
			}
		}
	}

	object Overflow : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.operand32) {
				val rel32 = processor80386.instructionReadS32()
				if (processor80386.FLAGS.OVERFLOW) {
					processor80386.EIP.qu += rel32.toUInt()
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JO rel32",
						arrayOf(rel32, processor80386.FLAGS.OVERFLOW)
					)
				)
			} else {
				val rel16 = processor80386.instructionReadS16()
				if (processor80386.FLAGS.OVERFLOW) {
					processor80386.EIP.qu += rel16.toUInt()
					processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JO rel16",
						arrayOf(rel16, processor80386.FLAGS.OVERFLOW)
					)
				)
			}
		}
	}

	object NotLess : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.operand32) {
				val rel32 = processor80386.instructionReadS32()
				if (processor80386.FLAGS.SIGN == processor80386.FLAGS.OVERFLOW) {
					processor80386.EIP.qu += rel32.toUInt()
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JNL rel32",
						arrayOf(rel32, processor80386.FLAGS.SIGN == processor80386.FLAGS.OVERFLOW)
					)
				)
			} else {
				val rel16 = processor80386.instructionReadS16()
				if (processor80386.FLAGS.SIGN == processor80386.FLAGS.OVERFLOW) {
					processor80386.EIP.qu += rel16.toUInt()
					processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JNL rel16",
						arrayOf(rel16, processor80386.FLAGS.SIGN == processor80386.FLAGS.OVERFLOW)
					)
				)
			}
		}
	}

	object Less : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.operand32) {
				val rel32 = processor80386.instructionReadS32()
				if (processor80386.FLAGS.SIGN != processor80386.FLAGS.OVERFLOW) {
					processor80386.EIP.qu += rel32.toUInt()
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JL rel32",
						arrayOf(rel32, processor80386.FLAGS.SIGN != processor80386.FLAGS.OVERFLOW)
					)
				)
			} else {
				val rel16 = processor80386.instructionReadS16()
				if (processor80386.FLAGS.SIGN != processor80386.FLAGS.OVERFLOW) {
					processor80386.EIP.qu += rel16.toUInt()
					processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JL rel16",
						arrayOf(rel16, processor80386.FLAGS.SIGN != processor80386.FLAGS.OVERFLOW)
					)
				)
			}
		}
	}

	object LessOrEqual : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.operand32) {
				val rel32 = processor80386.instructionReadS32()
				if (processor80386.FLAGS.ZERO && (processor80386.FLAGS.SIGN != processor80386.FLAGS.OVERFLOW)) {
					processor80386.EIP.qu += rel32.toUInt()
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JLE rel32",
						arrayOf(
							rel32,
							processor80386.FLAGS.ZERO && (processor80386.FLAGS.SIGN != processor80386.FLAGS.OVERFLOW)
						)
					)
				)
			} else {
				val rel16 = processor80386.instructionReadS16()
				if (processor80386.FLAGS.ZERO && (processor80386.FLAGS.SIGN != processor80386.FLAGS.OVERFLOW)) {
					processor80386.EIP.qu += rel16.toUInt()
					processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JLE rel16",
						arrayOf(
							rel16,
							processor80386.FLAGS.ZERO && (processor80386.FLAGS.SIGN != processor80386.FLAGS.OVERFLOW)
						)
					)
				)
			}
		}
	}

	object Greater : JumpShortIfConditionIsMetInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.operand32) {
				val rel32 = processor80386.instructionReadS32()
				if (!processor80386.FLAGS.ZERO && (processor80386.FLAGS.SIGN == processor80386.FLAGS.OVERFLOW)) {
					processor80386.EIP.qu += rel32.toUInt()
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JG rel32",
						arrayOf(
							rel32,
							!processor80386.FLAGS.ZERO && (processor80386.FLAGS.SIGN == processor80386.FLAGS.OVERFLOW)
						)
					)
				)
			} else {
				val rel16 = processor80386.instructionReadS16()
				if (!processor80386.FLAGS.ZERO && (processor80386.FLAGS.SIGN == processor80386.FLAGS.OVERFLOW)) {
					processor80386.EIP.qu += rel16.toUInt()
					processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
				}
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JG rel16",
						arrayOf(
							rel16,
							!processor80386.FLAGS.ZERO && (processor80386.FLAGS.SIGN == processor80386.FLAGS.OVERFLOW)
						)
					)
				)
			}
		}
	}
}