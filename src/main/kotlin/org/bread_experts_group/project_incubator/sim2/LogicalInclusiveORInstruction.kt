package org.bread_experts_group.project_incubator.sim2

sealed class LogicalInclusiveORInstruction : Instruction {
	object EAXImm1632 : LogicalInclusiveORInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.operand32) {
				val imm32 = processor80386.instructionReadU32K()
				val result = processor80386.EAX.qu or imm32
				processor80386.EAX.qu = result
				computeZFPFSF32(processor80386, result)
				processor80386.FLAGS.CARRY = false
				processor80386.FLAGS.OVERFLOW = false
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"OR EAX,imm32",
						arrayOf(imm32),
						arrayOf(
							"CF=false",
							"OF=false",
							"ZF=${processor80386.FLAGS.ZERO}",
							"SF=${processor80386.FLAGS.SIGN}",
							"PF=${processor80386.FLAGS.PARITY}"
						)
					)
				)
			} else {
				val imm16 = processor80386.instructionReadU16K()
				val result = processor80386.AX.du or imm16
				processor80386.AX.du = result
				computeZFPFSF16(processor80386, result)
				processor80386.FLAGS.CARRY = false
				processor80386.FLAGS.OVERFLOW = false
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"OR AX,imm16",
						arrayOf(imm16),
						arrayOf(
							"CF=false",
							"OF=false",
							"ZF=${processor80386.FLAGS.ZERO}",
							"SF=${processor80386.FLAGS.SIGN}",
							"PF=${processor80386.FLAGS.PARITY}"
						)
					)
				)
			}
		}
	}

	object ModRM8Imm8 : LogicalInclusiveORInstruction() {
		override fun execute(processor80386: Processor80386) {
			val (mod, reg, rm) = decompose233(processor80386.instructionReadS8())
			val sRegister = rb(processor80386, reg)
			if (processor80386.address32) {
				val (dAddress, dRegister) = modRm32Addressing(processor80386, mod, rm, true)
				if (dAddress != null) {
					val result = processor80386.memorySource.readU8K(dAddress) or sRegister.bu
					processor80386.memorySink.writeU8K(dAddress, result)
					computeZFPFSF8(processor80386, result)
					processor80386.FLAGS.CARRY = false
					processor80386.FLAGS.OVERFLOW = false
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"OR r/m8,r8",
							arrayOf(dAddress, sRegister.label),
							arrayOf(
								"CF=false",
								"OF=false",
								"ZF=${processor80386.FLAGS.ZERO}",
								"SF=${processor80386.FLAGS.SIGN}",
								"PF=${processor80386.FLAGS.PARITY}"
							)
						)
					)
				} else TODO("REG")
			} else {
				val (dAddress, dRegister) = modRm16Addressing(processor80386, mod, rm, true)
				if (dAddress != null) TODO("Addr")
				else TODO("REG")
			}
		}
	}

	class ModRM1632Imm1632(val mod: Int, val rm: Int) : LogicalInclusiveORInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.address32) {
				if (processor80386.operand32) TODO("A32 O32")
				else TODO("A32 O16")
			} else {
				val (dAddress, dRegister) = modRm16Addressing(processor80386, mod, rm)
				if (processor80386.operand32) TODO("A16 O32")
				else {
					val imm16 = processor80386.instructionReadU16K()
					if (dAddress != null) TODO("ADDR")
					else {
						val result = (dRegister as Register16).du or imm16
						dRegister.du = result
						processor80386.FLAGS.CARRY = false
						processor80386.FLAGS.OVERFLOW = false
						computeZFPFSF16(processor80386, result)
						processor80386.logger.log(
							InstructionExecutionLogIdentifier(
								"OR r/m16,imm16",
								arrayOf(dRegister.label, imm16),
								arrayOf(
									"CF=false",
									"OF=false",
									"ZF=${processor80386.FLAGS.ZERO}",
									"SF=${processor80386.FLAGS.SIGN}",
									"PF=${processor80386.FLAGS.PARITY}"
								)
							)
						)
					}
				}
			}
		}
	}
}