package org.bread_experts_group.project_incubator.sim2

sealed class AddInstruction : Instruction {
	class ModRM1632Imm1632(val mod: Int, val rm: Int) : AddInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.address32) {
				if (processor80386.operand32) TODO("A32 O32")
				else TODO("A32 O16")
			} else {
				val (dAddress, dRegister) = modRm16Addressing(processor80386, mod, rm)
				val imm8 = processor80386.instructionReadS8()
				if (processor80386.operand32) TODO("A16 O32")
				else {
					if (dAddress != null) TODO("A")
					else {
						val addend1 = (dRegister as Register16).du
						val addend2 = imm8.toUShort()
						val result = computeCarry16(processor80386, addend1, addend2, true)
						computeZFPFSF16(processor80386, result)
						dRegister.du = result
						processor80386.logger.log(
							InstructionExecutionLogIdentifier(
								"ADD r/m16,imm8",
								arrayOf(addend1, addend2),
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
			}
		}
	}

	object EAXmm1632 : AddInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.operand32) {
				val addend1 = processor80386.EAX.qu
				val addend2 = processor80386.instructionReadU32K()
				val result = computeCarry32(processor80386, addend1, addend2, true)
				computeZFPFSF32(processor80386, result)
				processor80386.EAX.qu = result
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"ADD EAX,imm32",
						arrayOf(addend1, addend2),
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
			} else TODO("O16")
		}
	}

	object ModRM1632Reg : AddInstruction() {
		override fun execute(processor80386: Processor80386) {
			val (mod, reg, rm) = decompose233(processor80386.instructionReadS8())
			if (processor80386.address32) {
				if (processor80386.operand32) TODO("A32 O32")
				else TODO("A32 O16")
			} else {
				val (dAddress, dRegister) = modRm16Addressing(processor80386, mod, rm)
				if (dAddress != null) TODO("ADDR")
				if (processor80386.operand32) {
					val sRegister = rd(processor80386, reg)
					val addend1 = (dRegister as Register32).qu
					val addend2 = sRegister.qu
					val result = computeCarry32(processor80386, addend1, addend2, true)
					computeZFPFSF32(processor80386, result)
					dRegister.qu = result
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"ADD r/m32,r32",
							arrayOf(addend1, addend2),
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
				} else TODO("A16 O16")
			}
		}
	}
}