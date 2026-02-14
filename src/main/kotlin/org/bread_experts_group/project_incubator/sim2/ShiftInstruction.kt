package org.bread_experts_group.project_incubator.sim2


sealed class ShiftInstruction : Instruction {
	sealed class Left : ShiftInstruction() {
		class Once(val mod: Int, val rm: Int) : Left() {
			override fun execute(processor80386: Processor80386) {
				if (processor80386.address32) {
					TODO("A32")
				} else {
					val (dAddress, dRegister) = modRm16Addressing(processor80386, mod, rm, true)
					if (dAddress != null) TODO("Address")
					dRegister as Register8
					processor80386.FLAGS.CARRY = dRegister.bu.toUInt() and 0b10000000u != 0u
					dRegister.bu = (dRegister.bu.toUInt() shl 1).toUByte()
					val dRqu = dRegister.bu.toUInt()
					val hob = dRqu and 0b10000000u != 0u
					processor80386.FLAGS.OVERFLOW = hob != processor80386.FLAGS.CARRY
					processor80386.FLAGS.ZERO = dRqu == 0u
					processor80386.FLAGS.SIGN = (dRqu shr 7) != 0u
					processor80386.FLAGS.PARITY = (dRqu and 0xFFu).countOneBits() % 2 == 0
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"SHL r/m8,1",
							arrayOf(dRegister.label),
							arrayOf(
								"CF=${processor80386.FLAGS.CARRY}",
								"OF=${processor80386.FLAGS.OVERFLOW}",
								"ZF=${processor80386.FLAGS.ZERO}",
								"SF=${processor80386.FLAGS.SIGN}",
								"PF=${processor80386.FLAGS.PARITY}",
							)
						)
					)
				}
			}
		}

		class ModRM1632Imm8(val mod: Int, val rm: Int) : Left() {
			override fun execute(processor80386: Processor80386) {
				if (processor80386.address32) TODO("A32")
				else {
					val (dAddress, dRegister) = modRm16Addressing(processor80386, mod, rm)
					val imm8 = processor80386.instructionReadU8I()
					if (processor80386.operand32) {
						if (dAddress != null) TODO("Address")
						dRegister as Register32
						var temp = imm8
						while (temp != 0) {
							processor80386.FLAGS.CARRY = (dRegister.qu shr 31) and 1u != 0u
							dRegister.qu = dRegister.qu shl 1
							temp--
						}
						computeZFPFSF32(processor80386, dRegister.qu)
						processor80386.logger.log(
							InstructionExecutionLogIdentifier(
								"SHL r/m32,imm8",
								arrayOf(dRegister.label, imm8),
								arrayOf(
									"CF=${processor80386.FLAGS.CARRY}",
									"ZF=${processor80386.FLAGS.ZERO}",
									"SF=${processor80386.FLAGS.SIGN}",
									"PF=${processor80386.FLAGS.PARITY}",
								)
							)
						)
					} else TODO("O16")
				}
			}
		}
	}

	sealed class Right : ShiftInstruction() {
		class ModRM1632Imm8(val mod: Int, val rm: Int) : Right() {
			override fun execute(processor80386: Processor80386) {
				if (processor80386.address32) {
					TODO("A32")
				} else {
					val (dAddress, dRegister) = modRm16Addressing(processor80386, mod, rm)
					val imm8 = processor80386.instructionReadU8I()
					if (processor80386.operand32) {
						if (dAddress != null) TODO("Address")
						dRegister as Register32
						var temp = imm8
						while (temp != 0) {
							processor80386.FLAGS.CARRY = dRegister.qu and 1u != 0u
							dRegister.qu = dRegister.qu shr 1
							temp--
						}
						computeZFPFSF32(processor80386, dRegister.qu)
						processor80386.logger.log(
							InstructionExecutionLogIdentifier(
								"SHR r/m32,imm8",
								arrayOf(dRegister.label, imm8),
								arrayOf(
									"CF=${processor80386.FLAGS.CARRY}",
									"ZF=${processor80386.FLAGS.ZERO}",
									"SF=${processor80386.FLAGS.SIGN}",
									"PF=${processor80386.FLAGS.PARITY}",
								)
							)
						)
					} else {
						if (dAddress != null) TODO("Address")
						dRegister as Register16
						var temp = imm8
						while (temp != 0) {
							processor80386.FLAGS.CARRY = dRegister.du and 1u != 0u.toUShort()
							dRegister.du = (dRegister.du.toUInt() shr 1).toUShort()
							temp--
						}
						computeZFPFSF16(processor80386, dRegister.du)
						processor80386.logger.log(
							InstructionExecutionLogIdentifier(
								"SHR r/m16,$imm8",
								arrayOf(dRegister.label),
								arrayOf(
									"CF=${processor80386.FLAGS.CARRY}",
									"ZF=${processor80386.FLAGS.ZERO}",
									"SF=${processor80386.FLAGS.SIGN}",
									"PF=${processor80386.FLAGS.PARITY}",
								)
							)
						)
					}
				}
			}
		}
	}
}